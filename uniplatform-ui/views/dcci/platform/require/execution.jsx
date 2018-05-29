import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Pagination,DateTime,Text} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '执行情况',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

class TaskDetail extends Component {
  constructor(props){
    super(props);

    this.state = {
      index:1,
      size:20,
      total:0,
      id : this.props.page.id,
      taskDetails : [],
      crawlerType : [],
      statusType : [],
      taskAssignType : [],
      name : '',
      type : '' ,
      assignType : '',
      siteTotal: '',
      status : '',
      siteRepeat:''
    }
    this.getFetchTaskDetail = this.getFetchTaskDetail.bind(this);
    this.getFetchEnum = this.getFetchEnum.bind(this);
    this.getFetchTaskSingle = this.getFetchTaskSingle.bind(this);
  }

  componentDidMount() {
    this.getFetchEnum();
    this.getFetchTaskDetail();
    this.getFetchTaskSingle();
  }


  getFetchEnum(){
    fetch( `${Uniplatform.context.url}/dcci/task/manage/list/enum` , {
    credentials: 'same-origin',
    method: 'POST'
    } )
      .then( ( res ) => res.json() )
      .then( ( tasks ) => {
        let crawlerType = tasks.data.crawlertype;
        let statusType = tasks.data.statustype;
        let taskAssignType = tasks.data.taskassigntype;
        this.setState({crawlerType,statusType,taskAssignType});
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  getFetchTaskDetail(){
    let param = new FormData();
    let { size,index,id } = this.state;
    param.append( 'size', size );
    param.append( 'number', index);
    fetch( `${Uniplatform.context.url}/dcci/task/manage/view/${id}` , {
    credentials: 'same-origin',
    method: 'POST',
    body : param
    } )
      .then( ( res ) => res.json() )
      .then( ( sites ) => {
        let taskDetails = sites.data.content;
        let total = sites.data.totalElements;
        this.setState({taskDetails,total});
        if (sites.data.content.length <= 0){
          popup(<Snackbar message="抱歉，暂时没有数据"/>);
        }
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  getFetchTaskSingle(){
    fetch( `${Uniplatform.context.url}/dcci/task/manage/get/${this.state.id}` , {
    credentials: 'same-origin',
    method: 'POST'
    } )
      .then( ( res ) => res.json() )
      .then( ( sites ) => {
        let name = sites.data.name;
        let type = sites.data.type;
        let assignType = sites.data.assignType;
        let siteTotal = sites.data.siteTotal;
        let status = sites.data.status;
        let siteRepeat = sites.data.siteRepeat;
        this.setState({name,type,assignType,siteTotal,status,siteRepeat});
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  exChangePagi(index, size) {
    this.setState({index, size}, () => this.getFetchTaskDetail());
  }

  getType(data,dataSource){
    let result = '';
    let { statusType,crawlerType,taskAssignType } = this.state;
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
    }else if(dataSource == 'taskAssignType'){
       taskAssignType.map((item,indexType) => {
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

  render() {
    let {taskDetails,index,size,total,name,type,assignType,siteTotal,status,siteRepeat} = this.state;
    return (
        <Page>
            <SingleBanner prefix="数据中心" id={''}/>
            <Divider />
            <Row>
               <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                  <Com_Menu url={'/dcci/platform/require/list'} />
                </Col>
                <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} style={{minHeight:'600px'}}>
                <NavigationBar code={'execution'} innerId={this.state.id}/>
                <Row>
                  <Col size={{ normal: 24, small: 24, medium: 8, large: 8 }}>
                    <Text>任务名 : {name}</Text>
                    <Text>分配方式 : {this.getType(assignType,'taskAssignType')}</Text>
                  </Col>
                  <Col size={{ normal: 24, small: 24, medium: 8, large: 8 }}>
                    <Text>任务类型 : {this.getType(type,'crawlerType')}</Text>
                    <Text>上传总数 : {siteTotal}</Text>
                  </Col>
                  <Col size={{ normal: 24, small: 24, medium: 8, large: 8 }}>
                    <Text>重复数 : {siteRepeat}</Text>
                    <Text>状态 : {this.getType(status,'statusType')}</Text>
                  </Col>
                </Row>
                  <Divider/>

                  <Table dataSource={taskDetails} striped={ true } complex headBolder={ true } >
                    <Column dataIndex="createdUser" title="分配人" textAlign="center" scaleWidth="15%"/>
                    <Column title="执行人" textAlign="center" scaleWidth="15%">
                    { ( value ) => {
                          return value.sysUser.account;
                      } }
                    </Column>
                    <Column title="截止时间" textAlign="center" scaleWidth="16%">
                    { ( value ) => {
                          return (<div style={{lineHeight:'35px'}}><DateTime format='yyyy/MM/dd hh:mm:ss' content={value.deadline} /></div> );
                      } }
                    </Column>
                    <Column dataIndex="siteAssign" title="总数" textAlign="center" scaleWidth="12%"/>
                    <Column dataIndex="siteComplete" title="已完成" textAlign="center" scaleWidth="12%"/>
                    <Column dataIndex="siteFail" title="无法配置" textAlign="center" scaleWidth="15%"/>
                    <Column title="状态" textAlign="center" scaleWidth="15%">
                     { ( value ) => {
                          let result = this.getSuccessType(value.success);
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
TaskDetail.UIPage = page;
export default TaskDetail;