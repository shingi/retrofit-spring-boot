package cn.dysania.retrofit.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author baitouweng
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetrofitClient {

    String value() default "";

    /**
     * clientName vlaue 或 name 不能为空
     */
    String name() default "";

    String url() default "";

    /**
     * client bean alias
     */
    String qualifier() default "";

    boolean primary() default true;

    /**
     * 指定client配置类
     */
    Class<?>[] configuration() default {};
}
