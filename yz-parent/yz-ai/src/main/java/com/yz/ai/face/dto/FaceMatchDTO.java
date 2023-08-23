package com.yz.ai.face.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName FaceMatchDTO
 * @Description 百度人脸比对API请求参数
 * @Author 3hgh
 * @Date 2023/1/30 11:48
 * @Version 1.0
 */
@Data
public class FaceMatchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片信息(总数据大小应小于10M，图片尺寸在1920x1080以下)，图片上传方式根据image_type来判断。 两张图片通过json格式上传，格式参考表格下方示例
     * [必选]
     */
    private String image;

    /**
     * 图片类型
     * BASE64:图片的base64值，base64编码后的图片数据，编码后的图片大小不超过2M；
     * URL:图片的 URL地址( 可能由于网络等原因导致下载图片时间过长)；
     * FACE_TOKEN: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个。
     * [必选]
     */
    private String image_type;

    /**
     * 人脸的类型
     * LIVE：表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等，
     * IDCARD：表示身份证芯片照：二代身份证内置芯片中的人像照片，
     * WATERMARK：表示带水印证件照：一般为带水印的小图，如公安网小图
     * CERT：表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片
     * INFRARED 表示红外照片：使用红外相机拍摄的照片
     * HYBRID：表示混合类型，如果传递此值时会先对图片进行检测判断所属类型(生活照 or 证件照)（仅针对请求参数 image_type 为 BASE64 或 URL 时有效）
     * 默认LIVE
     */
    private String face_type;

    /**
     * 图片质量控制
     * NONE: 不进行控制
     * LOW:较低的质量要求
     * NORMAL: 一般的质量要求
     * HIGH: 较高的质量要求
     * 默认 NONE
     * 若图片质量不满足要求，则返回结果中会提示质量检测失败
     */
    private String quality_control;

    /**
     * 活体检测控制
     * NONE: 不进行控制
     * LOW:较低的活体要求(高通过率 低攻击拒绝率)
     * NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)
     * HIGH: 较高的活体要求(高攻击拒绝率 低通过率)
     * 默认 NONE
     * 若活体检测结果不满足要求，则返回结果中会提示活体检测失败
     */
    private String liveness_control;

    /**
     * 人脸检测排序类型
     * 0:代表检测出的人脸按照人脸面积从大到小排列
     * 1:代表检测出的人脸按照距离图片中心从近到远排列
     * 默认为0
     */
    private Integer face_sort_type;

    /**
     * 合成图控制参数
     * NONE: 不进行控制
     * LOW:较低的合成图阈值数值，由于合成图判定逻辑为大于阈值视为合成图攻击，该项代表低通过率、高攻击拒绝率
     * NORMAL: 一般的合成图阈值数值，由于合成图判定逻辑为大于阈值视为合成图攻击，该项代表平衡的攻击拒绝率, 通过率
     * HIGH: 较高的合成图阈值数值，由于合成图判定逻辑为大于阈值视为合成图攻击，该项代表高通过率、低攻击拒绝率
     * 默认为NONE
     */
    private String spoofing_control;
}
