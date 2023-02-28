package io.github.wulilh.simplerpc.common.annotation;

import java.lang.annotation.*;

/**
 * @author wuliling Created By 2023-02-28 17:09
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {
}
