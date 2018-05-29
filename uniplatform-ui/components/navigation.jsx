import React, { Component } from 'react';
import { Col,Icon } from 'epm-ui';

class NavigationBar extends Component {
  constructor( props ) {
    super( props );
  }
  getOpenApiList(){
    window.open(`/dcci/platform/data/list`,'_self');
  }
  getOpenApiDetail(){
    window.open(`/dcci/platform/data/detail?apiId=${this.props.innerId}`,'_self');
  }
  getOpenApiExample(){
    window.open(`/dcci/platform/data/example?id=${this.props.id}&apiId=${this.props.innerId}`,'_self');
  }
  getOpenCodeList(){
    window.open(`/dcci/platform/data/codes`,'_self');
  }
  getOpenSiteList(){
    window.open(`/dcci/platform/site/list`,'_self');
  }
  getOpenTaskList(){
    window.open(`/dcci/platform/task/list`,'_self');
  }
  getOpencurrentSiteList(){
    window.open(`/dcci/platform/task/sites?taskRelation=${this.props.innerId}`,'_self');
  }
  getOpenRequireList(){
    window.open(`/dcci/platform/require/list`,'_self');
  }
  getOpenUpload(){
    window.open(`/dcci/platform/require/upload`,'_self');
  }
  getOpenExecution(){
    window.open(`/dcci/platform/require/execution?id=${this.props.innerId}`,'_self');
  }
  getOpenReqSite(){
    window.open(`/dcci/platform/require/sites?taskRelation=${this.props.innerId}`,'_self');
  }
  getOpenAssignTask(){
    window.open(`/dcci/platform/require/assign?id=${this.props.innerId}`,'_self');
  }
  getOpenComponentList(){
    window.open(`/dcci/platform/component/list`,'_self');
  }
  getOpenMachineList(){
      window.open(`/dcci/platform/machine/list`,'_self');
  }
  getOpenMac_pwdList(){
      window.open(`/dcci/platform/machine/users?id=${this.props.innerId}`,'_self');
  }
  getOpenApiAdd(){
    window.open(`/dcci/platform/data/add`,'_self');
  }
  getOpenApiUpdate(){
    window.open(`/dcci/platform/data/update?id=${this.props.innerId}`,'_self');
  }

  getOpenClusterList(){
    window.open(`/usou/clusterDetail?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }
  getOpenClusterNameList(){
    window.open(`/usou/clusterList`,'_self');
  }
  getOpenIndexList(){
    window.open(`/usou/indexManage?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }
  getOpenData_BrowseList(){
    window.open(`/usou/searchData_Browse?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }
  getOpenData_BasicList(){
    window.open(`/usou/searchData_Basic?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }
  getOpenData_ComplexList(){
    window.open(`/usou/searchData_Complex?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }
  getOpenData_SqlList(){
    window.open(`/usou/searchData_Sql?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }
  getOpenDicManage(){
    window.open(`/usou/dicManage?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }
  getOpenParticiple_TestList(){
    window.open(`/usou/participleManage_Test?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }
  getOpenMonitor_ResultList(){
    window.open(`/usou/monitorManage_Result?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }
  getOpenMonitor_RuleList(){
    window.open(`/usou/monitorManage_Rule?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }
  getOpenNodeList(){
    window.open(`/usou/nodeList?id=${this.props.innerId}&name=${this.props.name}`,'_self');
  }

  selectNav(code){
    let result;
    switch(code){
      case 'apiList' : result = (<a onClick={this.getOpenApiList.bind(this)}>商城管理</a>);
        break;
      case 'apiDetail' :  
        result = (<span><a onClick={this.getOpenApiList.bind(this)}>商城管理</a> / <a onClick={this.getOpenApiDetail.bind(this)}>api详情</a></span>);
        break;
      case 'apiExample' : 
        result = (<span><a onClick={this.getOpenApiList.bind(this)}>商城管理</a> / <a onClick={this.getOpenApiDetail.bind(this)}>api详情</a> / <a onClick={this.getOpenApiExample.bind(this)}>示例</a></span>);
        break;
      case 'codeList' :
        result = (<span><a onClick={this.getOpenApiList.bind(this)}>商城管理</a> / <a onClick={this.getOpenCodeList.bind(this)}>状态码</a></span>);
        break;
      case 'siteManage' : result = (<a onClick={this.getOpenSiteList.bind(this)}>站点管理</a>);
        break;
      case 'taskList' : result = (<a onClick={this.getOpenTaskList.bind(this)}>任务管理</a>);
        break;
      case 'currentSiteList' :  result = (<span><a onClick={this.getOpenTaskList.bind(this)}>任务管理</a> / <a onClick={this.getOpencurrentSiteList.bind(this)}>查看采集点</a></span>);
        break;
      case 'requireManage' : result = (<a onClick={this.getOpenRequireList.bind(this)}>需求管理</a>);
        break;
      case 'upload' :  result = (<span><a onClick={this.getOpenRequireList.bind(this)}>需求管理</a> / <a onClick={this.getOpenUpload.bind(this)}>上传</a></span>);
        break;
      case 'execution' : result = (<span><a onClick={this.getOpenRequireList.bind(this)}>需求管理</a> / <a onClick={this.getOpenExecution.bind(this)}>执行情况</a></span>);
        break;
      case 'siteList' : result = (<span><a onClick={this.getOpenRequireList.bind(this)}>需求管理</a> / <a onClick={this.getOpenReqSite.bind(this)}>查看采集点</a></span>);
        break;
      case 'assignTask' : result = (<span><a onClick={this.getOpenRequireList.bind(this)}>需求管理</a> / <a onClick={this.getOpenAssignTask.bind(this)}>分配任务</a></span>);
        break;
      case 'componentManage' : result = (<a onClick={this.getOpenComponentList.bind(this)}>组件管理</a>);
        break;
      case 'machineManage' : result = (<a onClick={this.getOpenMachineList.bind(this)}>机器管理</a>);
        break;
      case 'mac_pwdManage' : result = (<span><a onClick={this.getOpenMachineList.bind(this)}>机器管理</a> / <a onClick={this.getOpenMac_pwdList.bind(this)}>用户管理</a></span>);
        break;
      case 'apiAdd' : result = (<span><a onClick={this.getOpenApiList.bind(this)}>商城管理</a> / <a onClick={this.getOpenApiAdd.bind(this)}>添加</a></span>);
        break;
      case 'apiUpdate' :  result = (<span><a onClick={this.getOpenApiList.bind(this)}>商城管理</a> / <a onClick={this.getOpenApiUpdate.bind(this)}>修改</a></span>);
        break;

      case 'clusterDetail'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a></span>);
        break;
      case 'indexManage'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a onClick={this.getOpenIndexList.bind(this)}>索引管理</a></span>);
        break;
      case 'indicesDetail'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a onClick={this.getOpenIndexList.bind(this)}>索引管理</a> / <a>{this.props.indices}</a></span>);
        break;
      case 'searchData_Browse'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>数据查询</a> / <a onClick={this.getOpenData_BrowseList.bind(this)}>浏览数据</a></span>);
        break;
      case 'searchData_Basic'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>数据查询</a> / <a onClick={this.getOpenData_BasicList.bind(this)}>基本查询</a></span>);
        break;
      case 'searchData_Complex'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>数据查询</a> / <a onClick={this.getOpenData_ComplexList.bind(this)}>复合查询</a></span>);
        break;
      case 'searchData_Sql'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>数据查询</a> / <a onClick={this.getOpenData_SqlList.bind(this)}>Sql查询</a></span>);
        break;
      case 'participleManage_Dictionary'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>分词管理</a> / <a onClick={this.getOpenDicManage.bind(this)}>词典管理</a> / <a>{this.props.indices}</a></span>);
        break;
      case 'dicManage'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>分词管理</a> / <a onClick={this.getOpenDicManage.bind(this)}>词典管理</a></span>);
        break;
      case 'participleManage_Test'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>分词管理</a> / <a onClick={this.getOpenParticiple_TestList.bind(this)}>分词器测试</a></span>);
        break;
      case 'monitorManage_Result'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>集群监控管理</a> / <a onClick={this.getOpenMonitor_ResultList.bind(this)}>预警结果</a></span>);
        break;
      case 'monitorManage_Rule'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>集群监控管理</a> / <a onClick={this.getOpenMonitor_RuleList.bind(this)}>预警规则</a></span>);
        break;
      case 'nodeManage'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a onClick={this.getOpenNodeList.bind(this)}>节点管理</a></span>);
        break;
      case 'nodeDetail'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a onClick={this.getOpenNodeList.bind(this)}>节点管理</a> / <a>{this.props.node}</a></span>);
        break;
      case 'securityManage_Role'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>安全管理</a> / <a>角色管理</a></span>);
        break;
      case 'securityManage_User'+this.props.name : result = (<span><a onClick={this.getOpenClusterNameList.bind(this)}>管理首页</a> / <a onClick={this.getOpenClusterList.bind(this)}>{this.props.name}</a> / <a>安全管理</a> / <a>用户管理</a></span>);
        break;
      default : result = '';
        break;
    }
    return result;
  }

  render() {
    return (
      <div className = "banner_div">
        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="banner_col">
          <div className = "banner_title" >
            <span className = "banner_span"> {this.selectNav(this.props.code)} </span>
          </div>
        </Col>
      </div>
    );
  }

}

export default NavigationBar;
export {NavigationBar};