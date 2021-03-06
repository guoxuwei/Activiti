/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.runtime.conf;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.activiti.runtime.api.cmd.TaskCommands;
import org.activiti.runtime.api.cmd.impl.ClaimTaskImpl;
import org.activiti.runtime.api.cmd.impl.CompleteTaskImpl;
import org.activiti.runtime.api.cmd.impl.CreateTaskImpl;
import org.activiti.runtime.api.cmd.impl.ReleaseTaskImpl;
import org.activiti.runtime.api.cmd.impl.SetTaskVariablesImpl;
import org.activiti.runtime.api.cmd.impl.UpdateTaskImpl;
import org.activiti.runtime.api.cmd.result.impl.ClaimTaskResultImpl;
import org.activiti.runtime.api.cmd.result.impl.CompleteTaskResultImpl;
import org.activiti.runtime.api.cmd.result.impl.CreateTaskResultImpl;
import org.activiti.runtime.api.cmd.result.impl.ReleaseTaskResultImpl;
import org.activiti.runtime.api.cmd.result.impl.SetTaskVariablesResultImpl;
import org.activiti.runtime.api.cmd.result.impl.UpdateTaskResultImpl;
import org.activiti.runtime.api.model.Task;
import org.activiti.runtime.api.model.TaskCandidateGroup;
import org.activiti.runtime.api.model.TaskCandidateUser;
import org.activiti.runtime.api.model.impl.TaskCandidateGroupImpl;
import org.activiti.runtime.api.model.impl.TaskCandidateUserImpl;
import org.activiti.runtime.api.model.impl.TaskImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskModelAutoConfiguration {

    //this bean will be automatically injected inside boot's ObjectMapper
    @Bean
    public Module customizeTaskModelObjectMapper() {
        SimpleModule module = new SimpleModule("mapTaskRuntimeInterfaces",
                                               Version.unknownVersion());
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver(){
            //this is a workaround for https://github.com/FasterXML/jackson-databind/issues/2019
            //once version 2.9.6 is related we can remove this @override method
            @Override
            public JavaType resolveAbstractType(DeserializationConfig config,
                                                BeanDescription typeDesc) {
                return findTypeMapping(config, typeDesc.getType());
            }
        };
        resolver.addMapping(Task.class,
                            TaskImpl.class);
        resolver.addMapping(TaskCandidateUser.class,
                            TaskCandidateUserImpl.class);
        resolver.addMapping(TaskCandidateGroup.class,
                            TaskCandidateGroupImpl.class);


        module.setAbstractTypes(resolver);

        module.registerSubtypes(new NamedType(ClaimTaskImpl.class,
                                              TaskCommands.CLAIM_TASK.name()));
        module.registerSubtypes(new NamedType(ClaimTaskResultImpl.class,
                                              TaskCommands.CLAIM_TASK.name()));

        module.registerSubtypes(new NamedType(CreateTaskImpl.class,
                                              TaskCommands.CREATE_TASK.name()));
        module.registerSubtypes(new NamedType(CreateTaskResultImpl.class,
                                              TaskCommands.CREATE_TASK.name()));

        module.registerSubtypes(new NamedType(ReleaseTaskImpl.class,
                                              TaskCommands.RELEASE_TASK.name()));
        module.registerSubtypes(new NamedType(ReleaseTaskResultImpl.class,
                                              TaskCommands.RELEASE_TASK.name()));

        module.registerSubtypes(new NamedType(CompleteTaskImpl.class,
                                              TaskCommands.COMPLETE_TASK.name()));
        module.registerSubtypes(new NamedType(CompleteTaskResultImpl.class,
                                              TaskCommands.COMPLETE_TASK.name()));

        module.registerSubtypes(new NamedType(UpdateTaskImpl.class,
                                              TaskCommands.UPDATE_TASK.name()));
        module.registerSubtypes(new NamedType(UpdateTaskResultImpl.class,
                                              TaskCommands.UPDATE_TASK.name()));

        module.registerSubtypes(new NamedType(SetTaskVariablesImpl.class,
                                              TaskCommands.SET_TASK_VARIABLES.name()));
        module.registerSubtypes(new NamedType(SetTaskVariablesResultImpl.class,
                                              TaskCommands.SET_TASK_VARIABLES.name()));

        return module;
    }


}
