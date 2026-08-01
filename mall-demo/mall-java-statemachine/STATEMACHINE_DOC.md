
# Java 状态机实现案例文档

## 1. 概述

本模块实现了一个轻量级订单状态机，核心特点：

- **无框架依赖**：不依赖 Spring 等容器，纯 Java 实现
- **规则与业务分离**：状态机只维护状态迁移规则，业务逻辑独立实现
- **二维表设计**：使用 `Map<状态, Map<事件, 目标状态>>` 描述合法迁移

---

## 2. 核心概念

### 2.1 状态机三要素

| 要素 | 类型 | 说明 |
|-----|------|------|
| **状态 (State)** | `OrderStatus` | 订单所处的生命周期阶段 |
| **事件 (Event)** | `OrderEvent` | 触发状态迁移的业务动作 |
| **动作 (Action)** | `OrderEventAction` | 事件触发时执行的业务逻辑 |

### 2.2 订单状态定义

```java
public enum OrderStatus {
    CREATED,   // 订单已创建，待支付
    PAID,      // 已支付，待发货
    SHIPPED,   // 已发货，待确认完成
    FINISHED,  // 订单正常结束
    CANCELED   // 订单已取消
}
```

### 2.3 订单事件定义

```java
public enum OrderEvent {
    PAY,    // 用户支付
    SHIP,   // 商家发货
    FINISH, // 用户确认收货
    CANCEL  // 取消订单
}
```

---

## 3. 状态迁移规则

### 3.1 状态转移图

```
    CREATED ──PAY──→ PAID ──SHIP──→ SHIPPED ──FINISH──→ FINISHED
      │                   │
     CANCEL              CANCEL
      │                   │
      └───────────────────┴──→ CANCELED
```

### 3.2 合法迁移矩阵

| 当前状态 | PAY | SHIP | FINISH | CANCEL |
|---------|-----|------|--------|--------|
| CREATED | PAID | × | × | CANCELED |
| PAID | × | SHIPPED | × | CANCELED |
| SHIPPED | × | × | FINISHED | × |
| FINISHED | × | × | × | × |
| CANCELED | × | × | × | × |

### 3.3 状态机核心实现

状态转移表的构建与查询逻辑：

```java
public class OrderStateMachine {
    // 二维表：当前状态 → 事件 → 目标状态
    private final Map<OrderStatus, Map<OrderEvent, OrderStatus>> transitions;
    
    // 注册默认转移规则
    public OrderStateMachine() {
        addTransition(OrderStatus.CREATED, OrderEvent.PAY, OrderStatus.PAID);
        addTransition(OrderStatus.CREATED, OrderEvent.CANCEL, OrderStatus.CANCELED);
        addTransition(OrderStatus.PAID, OrderEvent.SHIP, OrderStatus.SHIPPED);
        addTransition(OrderStatus.PAID, OrderEvent.CANCEL, OrderStatus.CANCELED);
        addTransition(OrderStatus.SHIPPED, OrderEvent.FINISH, OrderStatus.FINISHED);
    }
    
    // 根据当前状态与事件计算下一状态
    public OrderStatus transit(OrderStatus currentStatus, OrderEvent event) {
        // 非法迁移抛出 IllegalStateException
    }
}
```

---

## 4. 业务动作分离设计

### 4.1 设计原则

状态机只负责**规则校验**，不包含业务逻辑。业务代码应实现 `OrderEventAction` 接口：

```java
@FunctionalInterface
public interface OrderEventAction {
    void execute(OrderContext order);
}
```

### 4.2 动作实现类

| 事件 | 动作类 | 职责 |
|-----|--------|------|
| PAY | `PayOrderAction` | 调用支付网关、预占库存、记录支付流水 |
| SHIP | `ShipOrderAction` | 创建物流单、写入运单号、通知承运商 |
| FINISH | `FinishOrderAction` | 释放库存、发送通知、更新订单完成时间 |
| CANCEL | `CancelOrderAction` | 回滚库存、发送通知、记录取消原因 |

### 4.3 支付动作示例

```java
public class PayOrderAction implements OrderEventAction {
    @Override
    public void execute(OrderContext order) {
        String txnId = "PAY-" + UUID.randomUUID().toString().substring(0, 8);
        order.setPayTransactionId(txnId);
        // 真实场景：调支付网关、幂等校验、与订单金额对账等
    }
}
```

---

## 5. 流程编排服务

### 5.1 OrderProcessService 职责

`OrderProcessService` 作为**编排枢纽**，协调状态机与业务动作：

```java
public class OrderProcessService {
    private final OrderStateMachine stateMachine;
    private final Map<OrderEvent, OrderEventAction> actions;
    
    public void fire(OrderContext order, OrderEvent event) {
        // 1. 校验迁移合法性
        OrderStatus from = order.getStatus();
        OrderStatus to = stateMachine.transit(from, event);
        
        // 2. 执行业务动作（失败则抛异常，不修改状态）
        OrderEventAction action = actions.get(event);
        action.execute(order);
        
        // 3. 更新状态（仅业务成功后）
        order.setStatus(to);
    }
}
```

### 5.2 推荐执行顺序

```
┌─────────────────────────────────────────────────────────────┐
│  1. 状态机校验 (OrderStateMachine.transit)                │
│     └─ 非法迁移 → 抛出 IllegalStateException               │
├─────────────────────────────────────────────────────────────┤
│  2. 执行业务动作 (OrderEventAction.execute)               │
│     └─ 业务失败 → 抛出 RuntimeException，状态不变           │
├─────────────────────────────────────────────────────────────┤
│  3. 更新状态 (OrderContext.setStatus)                      │
│     └─ 生产环境应在同一事务中写库                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. 项目结构

```
src/main/java/com/yz/statemachine/order/
├── action/                    # 业务动作实现
│   ├── CancelOrderAction.java   # 取消订单业务
│   ├── FinishOrderAction.java   # 完成订单业务
│   ├── PayOrderAction.java      # 支付业务
│   └── ShipOrderAction.java     # 发货业务
├── OrderContext.java          # 订单上下文（状态 + 业务字段）
├── OrderEvent.java            # 事件枚举
├── OrderEventAction.java      # 业务动作接口
├── OrderProcessService.java   # 流程编排服务
├── OrderStateMachine.java     # 核心状态机
├── OrderStateMachineDemo.java # 演示入口
└── OrderStatus.java           # 状态枚举
```

---

## 7. 使用示例

### 7.1 纯状态机模式（无业务）

```java
OrderStateMachine stateMachine = new OrderStateMachine();
OrderStatus status = OrderStatus.CREATED;

status = stateMachine.transit(status, OrderEvent.PAY);    // CREATED → PAID
status = stateMachine.transit(status, OrderEvent.SHIP);   // PAID → SHIPPED
status = stateMachine.transit(status, OrderEvent.FINISH); // SHIPPED → FINISHED

// 非法迁移抛出异常
stateMachine.transit(status, OrderEvent.CANCEL); // IllegalStateException
```

### 7.2 完整流程模式（状态机 + 业务）

```java
OrderProcessService process = new OrderProcessService();
OrderContext order = new OrderContext(10001L, OrderStatus.CREATED);

process.fire(order, OrderEvent.PAY);    // 支付 + 状态更新
process.fire(order, OrderEvent.SHIP);   // 发货 + 状态更新
process.fire(order, OrderEvent.FINISH); // 完成 + 状态更新

// 取消分支
OrderContext cancelOrder = new OrderContext(10002L, OrderStatus.CREATED);
process.fire(cancelOrder, OrderEvent.CANCEL); // 取消 + 状态更新
```

---

## 8. 扩展建议

### 8.1 生产环境优化

| 维度 | 建议 |
|-----|------|
| **持久化** | 使用数据库存储状态转移规则，支持动态配置 |
| **事务管理** | 状态更新与业务操作放在同一事务中 |
| **幂等性** | 为每个事件添加唯一标识，防止重复处理 |
| **日志审计** | 记录每次状态变更的时间、操作人、原因 |

### 8.2 扩展状态转移

如需支持更多状态或事件，只需：

1. 在 `OrderStatus` / `OrderEvent` 中添加枚举值
2. 在 `OrderStateMachine` 构造函数中注册新的转移规则
3. 实现对应的 `OrderEventAction` 接口

---

## 9. 总结

本实现体现了状态机模式的核心价值：

1. **集中管理状态规则**：所有状态迁移一目了然
2. **防非法状态**：自动校验不合法的状态转换
3. **业务与规则解耦**：状态机只负责规则，业务逻辑独立演进
4. **易于扩展**：新增状态/事件只需添加配置，无需修改核心逻辑
