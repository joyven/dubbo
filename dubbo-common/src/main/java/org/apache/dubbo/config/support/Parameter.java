/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config.support;

import java.lang.annotation.*;

/**
 * Parameter
 * 参数注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Parameter {

    /**
     * 参数的key，默认获取注解字段的字段名，如果遇到驼峰式字段且转为"."分割的小写字符串
     *
     * @return
     */
    String key() default "";

    // 如果设置为true，其注解字段的value就不能为空，否则抛出异常 IllegalStateException
    boolean required() default false;

    /**
     * 通过该注解决定当前注解字段是否作为参数
     * false-表示可以为参数，即不排除
     * true-表示不可以为参数，即排除该字段
     *
     * @return
     */
    boolean excluded() default false;

    /**
     * 转义，对于特殊字段，如果需要URL.encode()编码，则需要设置为true
     *
     * @return
     */
    boolean escaped() default false;

    /**
     * 如果该字段设置为true，会在兼容早期的版本中使用，解析方式参见
     *
     * @return
     * @link AbstractConfig#appendParameters 和 @link AbstractConfig#appendAttributes
     */
    boolean attribute() default false;

    /**
     * 是否对参数追加值，如果遇到参数容器中已有该key的配置，
     * 如果设置为true，则用逗号分割追加，否则覆盖
     *
     * @return
     */
    boolean append() default false;

    /**
     * if {@link #key()} is specified, it will be used as the key for the annotated property when generating url.
     * by default, this key will also be used to retrieve the config value:
     * <pre>
     * {@code
     *  class ExampleConfig {
     *      // Dubbo will try to get "dubbo.example.alias_for_item=xxx" from .properties, if you want to use the original property
     *      // "dubbo.example.item=xxx", you need to set useKeyAsProperty=false.
     *      @Parameter(key = "alias_for_item")
     *      public getItem();
     *  }
     * }
     * 如果key被指定了，当生成dubbo url的时候则使用注解的属性作为key。
     * 默认情况，该key被用于检索配置值。
     * {@code
     *  class ExampleConfig {
     *      // dubbo将会尝试从properties中查找"dubbo.example.alias_for_item=xxx"的配置项。
     *      // 如果想使用原始的属性"dubbo.example.item=xxx"，需要设置 useKeyAsProperty=false
     *      @Parameter(key="alias_for_item)
     *      public getItem();
     *  }
     * }
     * </pre>
     */
    boolean useKeyAsProperty() default true;

}