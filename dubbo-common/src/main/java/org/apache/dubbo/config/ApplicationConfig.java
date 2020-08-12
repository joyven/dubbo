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
package org.apache.dubbo.config;

import org.apache.dubbo.common.compiler.support.AdaptiveCompiler;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.infra.InfraAdapter;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.support.Parameter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.dubbo.common.constants.CommonConstants.APPLICATION_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.DUMP_DIRECTORY;
import static org.apache.dubbo.common.constants.CommonConstants.HOST_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.METADATA_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.SHUTDOWN_WAIT_KEY;
import static org.apache.dubbo.common.constants.QosConstants.ACCEPT_FOREIGN_IP;
import static org.apache.dubbo.common.constants.QosConstants.QOS_ENABLE;
import static org.apache.dubbo.common.constants.QosConstants.QOS_HOST;
import static org.apache.dubbo.common.constants.QosConstants.QOS_PORT;
import static org.apache.dubbo.config.Constants.DEVELOPMENT_ENVIRONMENT;
import static org.apache.dubbo.config.Constants.PRODUCTION_ENVIRONMENT;
import static org.apache.dubbo.config.Constants.TEST_ENVIRONMENT;


/**
 * The application info
 * 应用信息配置
 *
 * @export
 */
public class ApplicationConfig extends AbstractConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    private static final long serialVersionUID = 5508512956753757169L;

    /**
     * Application name
     * <p>
     * 描述：当前应用名称，用于注册中心计算应用间依赖关系，
     * 注意：消费者和提供者应用名不要一样，此参数不是匹配条件，
     * 你当前项目叫什么名字就填什么，和提供者消费者角色无关，
     * 比如：kylin应用调用了morgan应用的服务，则kylin项目配成kylin，
     * morgan项目配成morgan，可能kylin也提供其它服务给别人使用，
     * 但kylin项目永远配成kylin，这样注册中心将显示kylin依赖于morgan
     * <p>
     * 范围：服务治理
     * <p>
     * 是否必须：必填
     */
    private String name;

    /**
     * The application version
     * <p>
     * 描述：当前应用的版本
     * <p>
     * 范围：服务治理
     * <p>
     * 是否必须：否
     */
    private String version;

    /**
     * Application owner
     * 描述： 应用负责人，用于服务治理，请填写负责人公司邮箱前缀
     * <p>
     * 范围： 服务治理
     * <p>
     * 是否必须：否
     */
    private String owner;

    /**
     * Application's organization (BU)
     * 组织名称(BU或部门)，用于注册中心区分服务来源，此配置项建议不要使用autoconfig，
     * 直接写死在配置中，比如china,intl,itu,crm,asc,dw,aliexpress等
     */
    private String organization;

    /**
     * Architecture layer
     * 用于服务分层对应的架构。如，intl、china。不同的架构使用不同的分层。
     */
    private String architecture;

    /**
     * Environment, e.g. dev, test or production
     * 应用环境，如：develop/test/product，从代码可以看到仅仅支持者三种
     * 不同环境使用不同的缺省值，以及作为只用于开发测试功能的限制条件
     */
    private String environment;

    /**
     * Java compiler
     * Java字节码编译器，用于动态类的生成，可选：jdk或javassist
     */
    private String compiler;

    /**
     * The type of the log access
     * 日志输出方式，可选：slf4j,jcl,log4j,log4j2,jdk
     */
    private String logger;

    /**
     * Registry centers
     * 注册中心
     */
    private List<RegistryConfig> registries;
    /**
     * 注册中心ID列表
     */
    private String registryIds;

    /**
     * Monitor center
     * 监控中心
     */
    private MonitorConfig monitor;

    /**
     * Is default or not
     * 是否是默认
     */
    private Boolean isDefault;

    /**
     * Directory for saving thread dump
     * 线程dump目录
     */
    private String dumpDirectory;

    /**
     * Whether to enable qos or not
     * 是否开启qos
     */
    private Boolean qosEnable;

    /**
     * The qos host to listen
     * qos监听主机地址
     */
    private String qosHost;

    /**
     * The qos port to listen
     * qos监听端口
     */
    private Integer qosPort;

    /**
     * Should we accept foreign ip or not?
     * 是否允许外网访问qos
     */
    private Boolean qosAcceptForeignIp;

    /**
     * Customized parameters
     * 用户自定义参数
     */
    private Map<String, String> parameters;

    /**
     * Config the shutdown.wait
     * shutdown等待时间，将结果赋值给了系统变量
     * dubbo.service.shutdown.wait=shutwait
     */
    private String shutwait;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * Metadata type, local or remote, if choose remote, you need to further specify metadata center.
     * 元数据类型，仅为 local或者remote
     * 如果是remote，需要制定元数据中心
     */
    private String metadataType;

    /**
     * 是否注册为消费者
     */
    private Boolean registerConsumer;

    /**
     * 仓库
     */
    private String repository;

    public ApplicationConfig() {
    }

    public ApplicationConfig(String name) {
        setName(name);
    }

    /**
     * 生成的参数中的key为注解指定的key：application
     * 但是其value却是从"dubbo.application.name"属性中获取的
     *
     * @return
     */
    @Parameter(key = APPLICATION_KEY, required = true, useKeyAsProperty = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (StringUtils.isEmpty(id)) {
            id = name;
        }
    }

    /**
     * 生成的参数中的key为注解指定的key：application.version
     * 但是其value却是从"dubbo.application.application.version"属性中获取的
     *
     * @return
     */
    @Parameter(key = "application.version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        if (environment != null) {
            if (!(DEVELOPMENT_ENVIRONMENT.equals(environment)
                    || TEST_ENVIRONMENT.equals(environment)
                    || PRODUCTION_ENVIRONMENT.equals(environment))) {

                throw new IllegalStateException(String.format("Unsupported environment: %s, only support %s/%s/%s, default is %s.",
                        environment,
                        DEVELOPMENT_ENVIRONMENT,
                        TEST_ENVIRONMENT,
                        PRODUCTION_ENVIRONMENT,
                        PRODUCTION_ENVIRONMENT));
            }
        }
        this.environment = environment;
    }

    public RegistryConfig getRegistry() {
        return CollectionUtils.isEmpty(registries) ? null : registries.get(0);
    }

    public void setRegistry(RegistryConfig registry) {
        List<RegistryConfig> registries = new ArrayList<RegistryConfig>(1);
        registries.add(registry);
        this.registries = registries;
    }

    public List<RegistryConfig> getRegistries() {
        return registries;
    }

    @SuppressWarnings({"unchecked"})
    public void setRegistries(List<? extends RegistryConfig> registries) {
        this.registries = (List<RegistryConfig>) registries;
    }

    @Parameter(excluded = true)
    public String getRegistryIds() {
        return registryIds;
    }

    public void setRegistryIds(String registryIds) {
        this.registryIds = registryIds;
    }

    public MonitorConfig getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = new MonitorConfig(monitor);
    }

    public void setMonitor(MonitorConfig monitor) {
        this.monitor = monitor;
    }

    public String getCompiler() {
        return compiler;
    }

    public void setCompiler(String compiler) {
        this.compiler = compiler;
        AdaptiveCompiler.setDefaultCompiler(compiler);
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
        LoggerFactory.setLoggerAdapter(logger);
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Parameter(key = DUMP_DIRECTORY)
    public String getDumpDirectory() {
        return dumpDirectory;
    }

    public void setDumpDirectory(String dumpDirectory) {
        this.dumpDirectory = dumpDirectory;
    }

    @Parameter(key = QOS_ENABLE)
    public Boolean getQosEnable() {
        return qosEnable;
    }

    public void setQosEnable(Boolean qosEnable) {
        this.qosEnable = qosEnable;
    }

    @Parameter(key = QOS_HOST)
    public String getQosHost() {
        return qosHost;
    }

    public void setQosHost(String qosHost) {
        this.qosHost = qosHost;
    }

    @Parameter(key = QOS_PORT)
    public Integer getQosPort() {
        return qosPort;
    }

    public void setQosPort(Integer qosPort) {
        this.qosPort = qosPort;
    }

    @Parameter(key = ACCEPT_FOREIGN_IP)
    public Boolean getQosAcceptForeignIp() {
        return qosAcceptForeignIp;
    }

    public void setQosAcceptForeignIp(Boolean qosAcceptForeignIp) {
        this.qosAcceptForeignIp = qosAcceptForeignIp;
    }

    /**
     * The format is the same as the springboot, including: getQosEnableCompatible(), getQosPortCompatible(),
     * getQosAcceptForeignIpCompatible().
     *
     * @return
     */
    @Parameter(key = "qos-enable", excluded = true)
    public Boolean getQosEnableCompatible() {
        return getQosEnable();
    }

    public void setQosEnableCompatible(Boolean qosEnable) {
        setQosEnable(qosEnable);
    }

    @Parameter(key = "qos-host", excluded = true)
    public String getQosHostCompatible() {
        return getQosHost();
    }

    public void setQosHostCompatible(String qosHost) {
        this.setQosHost(qosHost);
    }

    @Parameter(key = "qos-port", excluded = true)
    public Integer getQosPortCompatible() {
        return getQosPort();
    }

    public void setQosPortCompatible(Integer qosPort) {
        this.setQosPort(qosPort);
    }

    @Parameter(key = "qos-accept-foreign-ip", excluded = true)
    public Boolean getQosAcceptForeignIpCompatible() {
        return this.getQosAcceptForeignIp();
    }

    public void setQosAcceptForeignIpCompatible(Boolean qosAcceptForeignIp) {
        this.setQosAcceptForeignIp(qosAcceptForeignIp);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getShutwait() {
        return shutwait;
    }

    public void setShutwait(String shutwait) {
        System.setProperty(SHUTDOWN_WAIT_KEY, shutwait);
        this.shutwait = shutwait;
    }

    @Parameter(excluded = true)
    public String getHostname() {
        if (hostname == null) {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                LOGGER.warn("Failed to get the hostname of current instance.", e);
                hostname = "UNKNOWN";
            }
        }
        return hostname;
    }

    @Override
    @Parameter(excluded = true)
    public boolean isValid() {
        return !StringUtils.isEmpty(name);
    }

    @Parameter(key = METADATA_KEY)
    public String getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(String metadataType) {
        this.metadataType = metadataType;
    }

    public Boolean getRegisterConsumer() {
        return registerConsumer;
    }

    public void setRegisterConsumer(Boolean registerConsumer) {
        this.registerConsumer = registerConsumer;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    @Override
    public void refresh() {
        super.refresh();
        appendEnvironmentProperties();
    }

    private void appendEnvironmentProperties() {
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        Set<InfraAdapter> adapters = ExtensionLoader.getExtensionLoader(InfraAdapter.class).getSupportedExtensionInstances();
        if (CollectionUtils.isNotEmpty(adapters)) {
            Map<String, String> inputParameters = new HashMap<>();
            inputParameters.put(APPLICATION_KEY, getName());
            inputParameters.put(HOST_KEY, getHostname());
            for (InfraAdapter adapter : adapters) {
                Map<String, String> extraParameters = adapter.getExtraAttributes(inputParameters);
                if (CollectionUtils.isNotEmptyMap(extraParameters)) {
                    parameters.putAll(extraParameters);
                }
            }
        }
    }
}
