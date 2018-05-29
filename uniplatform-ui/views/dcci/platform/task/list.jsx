import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Pagination,DateTime,Modal,ModalHeader,ModalBody,ModalFooter,Upload,Dialog} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '任务管理',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};
const contextUrl = '/uniplatform';

class TaskList extends Component {
  constructor(props){
    super(props);

    this.state = {
      index:1,
      size:20,
      total:0,
      name : '',
      status : '',
      levelType : '',
      type : '',
      success : '',
      taskList : [],
      crawlerType : [],
      levelTypes : [],
      visibleShow : false,
      visibleCheckShow : false,
      taskManage : '',
      show : false
    }
    this.getFetchTaskList = this.getFetchTaskList.bind(this);
    this.getFetchEnum = this.getFetchEnum.bind(this);
    this.beforeSubmit = this.beforeSubmit.bind(this);
    this.beforeUploadSub = this.beforeUploadSub.bind(this);
    this.afterUploadSub = this.afterUploadSub.bind(this);
    this.formTirgger = this.formTirgger.bind( this );
    this.formTirggerCheck = this.formTirggerCheck.bind(this);
    this.afterCheckSub = this.afterCheckSub.bind(this);
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
        let levelTypes = tasks.data.leveltype;
        this.setState({crawlerType,levelTypes});
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  getFetchTaskList(){
    let param = new FormData();
    let { size,name,url,levelType,index,type,success } = this.state;
    param.append( 'size', size );
    param.append( 'name', name );
    param.append( 'number', index);
    param.append( 'levelType', levelType );
    param.append( 'type', type );
    param.append('success', success);
    fetch( `${Uniplatform.context.url}/dcci/task/assign/view` , {
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
    let levelType = data.levelType;
    let name = data.name;
    let type = data.type;
    let success = data.success;
    this.setState({ levelType,name,type,success } , () => this.getFetchTaskList() );
  }

  exChangePagi(index, size) {
    this.setState({index, size}, () => this.getFetchTaskList());
  }

  getType(data,dataSource){
    let result = '';
    let { crawlerType,levelTypes } = this.state;
    if(dataSource == 'crawlerType'){
      crawlerType.map((item,indexType) => {
        for ( var index in item ) {
          if(index == data){
            result = item[data];
          }
        }
      });
      return result;
    }else if(dataSource == 'levelType'){
       levelTypes.map((item,indexType) => {
        for ( var index in item ) {
          if(index == data){
            result = item[data];
          }
        }
      });
      return result;
    }
  }

  getSuccessType(data){
    let result;
    switch ( data ) {
      case 0: result = '未开始';
        break;
      case 1: result = '开始配置';
        break;
      case 2: result = '任务完成';
        break;
      default: result = '';
        break;
    }
    return result;
  }

  getOperate(value){
    let success = value.success;
    let result ;
    if(success == 0){
      result = <p className="complex-link">
        <a onClick={this.getAssign.bind(this,value.id)}>开始配置</a>
        <a onClick={this.getCollectList.bind(this, value.manage.taskRelation)}>查看采集点</a>
      </p>;
    }else if(success == 1){
      result = <p className="complex-link">
        <a onClick={this.getUpload.bind(this,value.taskManage,'upload')}>上传配置</a>
        <a onClick={this.getCollectList.bind(this, value.manage.taskRelation)}>查看采集点</a>
        <a onClick={this.getUpload.bind(this,value.taskManage,'check')}>采集点校验</a>
      </p>;
    }else if(success == 2){
      result =  <p className="complex-link">
        <a onClick={this.getCollectList.bind(this, value.manage.taskRelation)}>查看采集点</a>
        <a onClick={this.getUpload.bind(this,value.taskManage,'check')}>采集点校验</a>
      </p>;
    }
    return result;
  }

  getCollectList(taskRelation){
    window.open(`/dcci/platform/task/sites?taskRelation=${taskRelation}`,'_self');
  }

  getAssign(id){
    fetch( `${Uniplatform.context.url}/dcci/task/assign/begin/${id}` , {
    credentials: 'same-origin',
    method: 'POST'
    } )
      .then( ( res ) => res.json() )
      .then( ( tasks ) => {
        if (tasks.code == '1'){
          popup(<Snackbar message={tasks.message} />);
          this.getFetchTaskList();
        }else{
          popup(<Snackbar message={tasks.message} />);
        }
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  getUpload(manageId,dataString){
    let taskManage = manageId;
    let visibleShow = false;
    let visibleCheckShow = false;
    if(dataString == 'upload'){
      visibleShow = true;
    }else if(dataString == 'check'){
      visibleCheckShow = true;
    }
    this.setState({visibleShow,taskManage,visibleCheckShow});
  }

  handleUpdateClose() {
    this.reset();
    this.setState({visibleShow: false});
  }

  handleCheckClose(){
    this.resetCheck();
    this.setState({visibleCheckShow: false});
  }

  formTirgger( trigger ) {
    this.reset = trigger.reset;
  }

  formTirggerCheck( trigger ) {
    this.resetCheck = trigger.reset;
  }

  beforeUploadSub(data){
    this.setState({ show : true });
    let manageId = this.state.taskManage;
    data["manageId"] = manageId;
    return data;
  }
  
  afterCheckSub(data){
    this.setState({ show : false });
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.handleCheckClose();
      this.getFetchTaskList();
    }else{
      popup(<Dialog 
            title="错误信息" 
            message={data.message} 
          />);
    }
  }

  afterUploadSub(data){
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.handleUpdateClose();
      this.getFetchTaskList();
    }else{
      popup(<Dialog 
            title="错误信息" 
            message={data.message} 
          />);
    }
  }

  render() {
    let {taskList,index,size,total,levelTypes,crawlerType,visibleShow,visibleCheckShow,show } = this.state;
    return (
        <Page>
            <SingleBanner prefix="数据中心" id={''}/>
            <Divider />
            <Row>
               <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                  <Com_Menu url={'/dcci/platform/task/list'}/>
                </Col>
                <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} style={{minHeight:'600px'}}>

                  <NavigationBar code={'taskList'} />

                  <Form type="inline"
                        action="fack" onSubmit={ this.beforeSubmit }>
                    <FormItem>
                      <Label>任务名:</Label>
                      <Input  name="name" placeholder="请输入任务名"/>
                    </FormItem>
                    <FormItem>
                      <Label>优先级:</Label>
                      <Select value={ 'ALL' } name="levelType">
                      <Option value='ALL'>全部</Option>
                      {
                        levelTypes.map((item,indexType) => {
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
                    <FormItem>
                      <Label>状态:</Label>
                      <Select value={ '-1' } name="success">
                        <Option value='-1'>全部</Option>
                        <Option value='0'>未开始</Option>
                        <Option value='1'>开始配置</Option>
                        <Option value='2'>任务完成</Option>
                      </Select>
                    </FormItem>
                    <Button htmlType="submit" type="default">查询</Button>
                  </Form>
                    
                  <Table dataSource={taskList} striped={ true } complex headBolder={ true } >
                    <Column title="任务名" textAlign="center" scaleWidth="15%">
                    {(value) => {
                      let result = value.manage.name;
                      return result;
                    } }
                    </Column>
                    <Column title="采集类型" textAlign="center" scaleWidth="10%">
                      { ( value ) => {
                          let result = this.getType(value.manage.type,'crawlerType');
                          return result;
                      } }
                    </Column>
                    <Column title="优先级" textAlign="center" scaleWidth="10%">
                     { ( value ) => {
                          let result = this.getType(value.manage.levelType,'levelType');
                          return result;
                      } }
                    </Column>
                    <Column dataIndex="createdUser" title="分配人" textAlign="center" scaleWidth="10%"/>
                    <Column dataIndex="siteAssign" title="分配(总)" textAlign="center" scaleWidth="9%"/>
                    <Column dataIndex="siteComplete" title="已完成" textAlign="center" scaleWidth="9%"/>
                    <Column dataIndex="siteFail" title="失败配置" textAlign="center" scaleWidth="9%"/>
                    <Column title="状态" textAlign="center" scaleWidth="8%">
                     { ( value ) => {
                          let result = this.getSuccessType(value.success);
                          return result;
                      } }
                    </Column>
                    <Column title="操作" textAlign="center" scaleWidth="20%">
                    { ( value ) => { 
                      let result = this.getOperate(value); 
                      return result;
                      }    
                    }
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

            <Form type="horizontal"
              method="post"
              action={ `${contextUrl}/dcci/task/assign/upload` }
              async={true}
              enctype="multipart/form-data"
              trigger={ this.formTirgger }
              onSubmit={this.beforeUploadSub}
              onAfterSubmit={this.afterUploadSub}
              >
              <Modal visible={visibleShow} onClose={this.handleUpdateClose.bind(this)}>
                  <ModalHeader>
                    上传配置
                  </ModalHeader>
                  <ModalBody>
                    <FormItem>
                      <Label>配置文件:</Label>
                      <Row>
                        <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} className="upload-filed">
                          <Upload placeholder="上传zip" name="file" limit={ 1 }/>
                          <span className="file">
                            <i className="fa fa-link"></i>
                            <a href={`${contextUrl}/dcci/task/assign/upload/example/download`} className="text"> 示例文件下载</a>
                          </span>
                        </Col>
                      </Row>
                    </FormItem>
                  </ModalBody>
                  <ModalFooter>
                  {
                    show ?
                    <div style={{float: 'left',color: '#0f5b99'}}>正在上传...</div>
                    : ''
                  }
                    <Button onClick={this.handleUpdateClose.bind(this)}>关闭</Button>
                    <Button type="primary" htmlType="submit">确定</Button>
                  </ModalFooter>
              </Modal>
            </Form>

            <Form type="horizontal"
              method="post"
              action={ `${contextUrl}/dcci/task/assign/check` }
              async={true}
              enctype="multipart/form-data"
              trigger={ this.formTirggerCheck }
              onSubmit={this.beforeUploadSub}
              onAfterSubmit={this.afterCheckSub}
              >
              <Modal visible={visibleCheckShow } onClose={this.handleCheckClose.bind(this)}>
                  <ModalHeader>
                    上传采集点
                  </ModalHeader>
                  <ModalBody>
                    <FormItem>
                      <Label>采集点:</Label>
                      <Row>
                        <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} className="upload-filed">
                          <Upload placeholder="上传Json" name="file" limit={ 1 }/>
                          <span className="file">
                            <i className="fa fa-link"></i>
                            <a href={`${contextUrl}/dcci/task/assign/check/example/download`} className="text"> 示例文件下载</a>
                          </span>
                        </Col>
                      </Row>
                    </FormItem>
                  </ModalBody>
                  <ModalFooter>
                    <Button onClick={this.handleCheckClose.bind(this)}>关闭</Button>
                    <Button type="primary" htmlType="submit">确定</Button>
                  </ModalFooter>
              </Modal>
            </Form>
        </Page>
    );
  }

}
TaskList.UIPage = page;
export default TaskList;