/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.engine.impl.persistence.entity.data.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.JobQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.CachedEntityMatcher;
import org.activiti.engine.impl.persistence.entity.TimerJobEntity;
import org.activiti.engine.impl.persistence.entity.TimerJobEntityImpl;
import org.activiti.engine.impl.persistence.entity.data.AbstractDataManager;
import org.activiti.engine.impl.persistence.entity.data.TimerJobDataManager;

/**
 * @author Tijs Rademakers
 * @author Vasile Dirla
 */
public class MybatisTimerJobDataManager extends AbstractDataManager<TimerJobEntity> implements TimerJobDataManager {

  public MybatisTimerJobDataManager(ProcessEngineConfigurationImpl processEngineConfiguration) {
    super(processEngineConfiguration);
  }

  @Override
  public Class<? extends TimerJobEntity> getManagedEntityClass() {
    return TimerJobEntityImpl.class;
  }

  @Override
  public TimerJobEntity create() {
    return new TimerJobEntityImpl();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<TimerJobEntity> findJobsByQueryCriteria(JobQueryImpl jobQuery, Page page) {
    String query = "selectWaitingTimerJobByQueryCriteria";
    return getDbSqlSession().selectList(query, jobQuery, page);
  }

  @Override
  public long findJobCountByQueryCriteria(JobQueryImpl jobQuery) {
    return (Long) getDbSqlSession().selectOne("selectWaitingTimerJobCountByQueryCriteria", jobQuery);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<TimerJobEntity> selectTimerJobsToDueDate(Page page) {
    Date now = getClock().getCurrentTime();
    return getDbSqlSession().selectList("selectTimerJobsToDueDate", now, page);
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public List<TimerJobEntity> findJobsByTypeAndProcessDefinitionId(String jobHandlerType, String processDefinitionId) {
    Map<String, String> params = new HashMap<String, String>(2);
    params.put("handlerType", jobHandlerType);
    params.put("processDefinitionId", processDefinitionId);
    return getDbSqlSession().selectList("selectWaitingTimerJobByTypeAndProcessDefinitionId", params);

  }

  @Override
  public Collection<TimerJobEntity> findJobsByExecutionId(final String executionId) {
    return getList("selectWaitingTimerJobsByExecutionId", executionId, new CachedEntityMatcher<TimerJobEntity>() {

      @Override
      public boolean isRetained(TimerJobEntity jobEntity) {
        return jobEntity.getExecutionId() != null && jobEntity.getExecutionId().equals(executionId);
      }
    }, true);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<TimerJobEntity> findJobsByTypeAndProcessDefinitionKeyNoTenantId(String jobHandlerType, String processDefinitionKey) {
    Map<String, String> params = new HashMap<String, String>(2);
    params.put("handlerType", jobHandlerType);
    params.put("processDefinitionKey", processDefinitionKey);
    return getDbSqlSession().selectList("selectWaitingTimerJobByTypeAndProcessDefinitionKeyNoTenantId", params);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<TimerJobEntity> findJobsByTypeAndProcessDefinitionKeyAndTenantId(String jobHandlerType, String processDefinitionKey, String tenantId) {
    Map<String, String> params = new HashMap<String, String>(3);
    params.put("handlerType", jobHandlerType);
    params.put("processDefinitionKey", processDefinitionKey);
    params.put("tenantId", tenantId);
    return getDbSqlSession().selectList("selectWaitingTimerJobByTypeAndProcessDefinitionKeyAndTenantId", params);
  }

  @Override
  public void updateJobTenantIdForDeployment(String deploymentId, String newTenantId) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("deploymentId", deploymentId);
    params.put("tenantId", newTenantId);
    getDbSqlSession().update("updateWaitingTimerJobTenantIdForDeployment", params);
  }

}