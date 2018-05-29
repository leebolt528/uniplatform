import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Pagination,DateTime,Dialog} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '需求管理',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

class RequireManage extends Component {
  constructor(props){
    super(props);

    this.state = {
      index:1,
      size:20,
      total:0,
      name : '',
      status : '',
      type : '',
      taskList : [],
      crawlerType : [],
      levelType : [],
      statusType : [],
      taskAssignType : []
    }
    this.getFetchTaskList = this.getFetchTaskList.bind(this);
    this.getFetchEnum = this.getFetchEnum.bind(this);
    this.beforeSubmit = this.beforeSubmit.bind(this);
  }

  componentDidMount() {
    this.getFetchEnum();
    this.getFetchTaskList();
  }

  getFetchEnum(){
    fetch( `${Uniplatform.context.url}/dcci/task/manage/list/enum` , {
    credentials: 'same-origin',
    method: 'POST'
    } )
      .then( ( res ) => res.json() )
      .then( ( tasks ) => {
        let crawlerType = tasks.data.crawlertype;
        let levelType = tasks.data.leveltype;
        let statusType = tasks.data.statustype;
        let taskAssignType = tasks.data.taskassigntype;
        this.setState({crawlerType,levelType,statusType,taskAssignType});
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  getFetchTaskList(){
    let param = new FormData();
    let { size,name,url,status,index,type } = this.state;
    param.append( 'size', size );
    param.append( 'name', name );
    param.append( 'number', index);
    param.append( 'status', status );
    param.append( 'type', type );
    fetch( `${Uniplatform.context.url}/dcci/task/manage/list` , {
    credentials: 'same-origin',
    method: 'POST',
    body:param
    } )
      .then( ( res ) => res.json() )
      .then( ( tasks ) => {
        let taskList = tasks.data.content;
        let total = tasks.data.totalElements;
        this.setState({taskList,total});
        if (taskList.length <= 0){
          popup(<Snackbar message="抱歉，暂时没有数据"/>);
        }
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  beforeSubmit(data){
    let status = data.status;
    let name = data.name;
    let type = data.type;
    this.setState({ status,name,type } , () => this.getFetchTaskList() );
  }

  exChangePagi(index, size) {
    this.setState({index, size}, () => this.getFetchTaskList());
  }

  getCollectList(taskRelation){
    window.open(`/dcci/platform/require/sites?taskRelation=${taskRelation}`,'_self');
  }

  getAssignask(id){
    window.open(`/dcci/platform/require/assign?id=${id}`,'_self');
  }

  getTaskDetail(id){
    window.open(`/dcci/platform/require/execution?id=${id}`,'_self');
  }

  getType(data,dataSource){
    let result = '';
    let { statusType,crawlerType,levelType } = this.state;
    if(dataSource == 'crawlerType'){
      crawlerType.map((item,indexType) => {
        for ( var index in item ) {
          if(index == data){
            result = item[data];
          }
        }
      });
      return result;
    }else if(dataSource == 'statusType'){
      statusType.map((item,indexType) => {
        for ( var index in item ) {
          if(index == data){
            result = item[data];
          }
        }
      });
      return result;
    }else if(dataSource == 'levelType'){
       levelType.map((item,indexType) => {
        for ( var index in item ) {
          if(index == data){
            result = item[data];
          }
        }
      });
      return result;
    }
  }

  getUpload(){
    window.open(`/dcci/platform/require/upload`,'_self');
  }

  getTaskDetele(id){
    const confirmShow = confirm("确定要删除该条数据?");
    if (!confirmShow) {
      return false;
    }
    fetch(`${Uniplatform.context.url}/dcci/task/manage/delete/${id}`, {
      credentials: 'same-origin',
      method: 'POST'
    })
      .then((res) => res.json())
      .then((data) => {
        if(data.code == '1'){
          this.getFetchTaskList();
          popup(<Snackbar message={data.message}/>)
        }else{
          popup(<Dialog 
            title="错误信息" 
            message={data.message} 
          />);
        }
      }).catch((err) => console.log(err.toString()));
  }

  getTaskRecall(id){
    const confirmShow = confirm("确定要撤回该条数据?");
    if (!confirmShow) {
      return false;
    }
    fetch(`${Uniplatform.context.url}/dcci/task/manage/recall/${id}`, {
      credentials: 'same-origin',
      method: 'POST'
    })
      .then((res) => res.json())
      .then((data) => {
        if(data.code == '1'){
          this.getFetchTaskList();
          popup(<Snackbar message={data.message}/>)
        }else{
          popup(<Dialog 
            title="错误信息" 
            message={data.message} 
          />);
        }
      }).catch((err) => console.log(err.toString()));
  }

  getStatusOpera(value){
    let nowStatus = value.status;
    let result ;
    if(nowStatus == 'EXECUTE'){
      result = <p className="complex-link">
                <a onClick={this.getTaskRecall.bind(this, value.id)}>撤回</a>
                <a onClick={this.getTaskDetele.bind(this, value.id)}>删除</a>
                <a onClick={this.getTaskDetail.bind(this, value.id)}>执行情况</a>
                <a onClick={this.getCollectList.bind(this, value.taskRelation)}>查看采集点</a>
              </p>;
    }else if(nowStatus == 'UNDISTRIBUTED' || nowStatus == 'RECALL'){
      result = <p className="complex-link">
                <a onClick={this.getTaskDetele.bind(this, value.id)}>删除</a>
                <a onClick={this.getAssignask.bind(this, value.id)}>分配任务 </a>
                <a onClick={this.getCollectList.bind(this, value.taskRelation)}>查看采集点 </a>
              </p>;
    }else if(nowStatus == 'COMPLETE'){
      result = <p className="complex-link">
                <a onClick={this.getTaskDetele.bind(this, value.id)}>删除</a>
                <a onClick={this.getTaskDetail.bind(this, value.id)}>执行情况</a>
                <a onClick={this.getCollectList.bind(this, value.taskRelation)}>查看采集点</a>
              </p>;
    }
    return result;
  }

  render() {
    let {taskList,index,size,total,statusType,crawlerType} = this.state;
    return (
        <Page>
            <SingleBanner prefix="数据中心" id={''}/>
            <Divider />
            <Row>
               <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                  <Com_Menu url={'/dcci/platform/require/list'}/>
                </Col>
                <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} style={{minHeight:'600px'}}>

                  <NavigationBar code={'requireManage'}/>

                  <Form type="inline"
                        action="fack" onSubmit={ this.beforeSubmit }>
                    <FormItem>
                      <Label>任务名:</Label>
                      <Input  name="name" placeholder="请输入任务名"/>
                    </FormItem>
                    <FormItem>
                      <Label>状态:</Label>
                      <Select value={ 'ALL' } name="status">
                      <Option value='ALL'>全部</Option>
                      {
                        statusType.map((item,indexType) => {
                          for ( var index in item ) {
                            return (<Option key={index} value={index}>{item[index]}</Option>)
                          }
                        })
                      }
                      </Select>
                    </FormItem>
                    <FormItem>
                      <Label>采集类型:</Label>
                      <Select value={ 'ALL' } name="type">
                      <Option value='ALL'>全部</Option>
                      {
                        crawlerType.map((item,indexType) => {
                          for ( var index in item ) {
                            return (<Option key={index} value={index}>{item[index]}</Option>)
                          }
                        })
                      }
                      </Select>
                    </FormItem>
                    <Button htmlType="submit" type="default">查询</Button>
                  </Form>

                  <Button type="default" size="small" style={{position:'relative',zIndex:1}} onClick={this.getUpload.bind(this)}>上传</Button>
                  <Divider/>
                    
                  <Table dataSource={taskList} striped={ true } complex headBolder={ true } >
                    <Column dataIndex="name" title="任务名" textAlign="center" scaleWidth="12%"/>
                    <Column title="采集类型" textAlign="center" scaleWidth="10%">
                      { ( value ) => {
                          let result = this.getType(value.type,'crawlerType');
                          return result;
                      } }
                    </Column>
                    <Column title="截至时间" textAlign="center" scaleWidth="16%">
                    { ( value ) => {
                          return (<div style={{lineHeight:'35px'}}><DateTime format='yyyy/MM/dd hh:mm:ss' content={value.deadline} /> </div>);
                      } }
                    </Column>
                    <Column dataIndex="siteTotal" title="上传(总)" textAlign="center" scaleWidth="10%"/>
                    <Column dataIndex="siteRepeat" title="重复(总)" textAlign="center" scaleWidth="10%"/>
                    <Column title="状态" textAlign="center" scaleWidth="8%">
                     { ( value ) => {
                          let result = this.getType(value.status,'statusType');
                          return result;
                      } }
                    </Column>
                    <Column title="优先级" textAlign="center" scaleWidth="10%">
                     { ( value ) => {
                          let result = this.getType(value.levelType,'levelType');
                          return result;
                      } }
                    </Column>
                    <Column title="操作" textAlign="center" scaleWidth="24%">
                    { ( value ) => {
                      let result = this.getStatusOpera(value);
                      return result;
                    } }
                    </Column>
                  </Table>
                  <Divider/>
                  <Pagination align='right' index={index} total={total} size={size}
                          onChange={this.exChangePagi.bind(this)}/>
                  <Divider/>
                </Col>    
              </Col>
            </Row>     
            <Footer />
        </Page>
    );
  }

}
RequireManage.UIPage = page;
export default RequireManage;