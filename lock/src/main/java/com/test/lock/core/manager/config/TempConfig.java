package com.test.lock.core.manager.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author heye
 * @Date 2020/1/15
 * @Decription
 * 不要在此处初始化字段的值！！！
 *  mess：存储临时配置信息，
 *  scope：仅当此请求
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TempConfig {
    private Boolean enableStatis;

    private Boolean enableFilter ;

    private Long expireMillis ;

    private Long waitMillis ;

    private String prefix ;

    private String suffix;

}
