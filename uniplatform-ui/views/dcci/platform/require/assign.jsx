import React, { Component } from 'react';
import {Page,Row,Col,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,DateTimePicker,Upload,Textarea,Dialog,Text,Modal,ModalHeader,ModalBody,ModalFooter,Table,Column,Pagination,Loading} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '分配任务',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

const contextUrl = '/uniplatform';

class AssignTask extends Component {
  constructor(props){
    super(props);
    this.state = {
      crawlerType : [],
      levelType : [],
      name : '',
      siteDataVis : false,
      index:1,
      size:5,
      total:0,
      ip:'',
      type:'',
      siteDatas : [],
      crawlerType : [],
      selectSite : [],
      userList : [],
      show : false
    }
    this.afterSubmit = this.afterSubmit.bind( this );
    this.beforeSubmit = this.beforeSubmit.bind(this);
    this.getFetchTaskDetail = this.getFetchTaskDetail.bind(this);
    this.getFetchEnum = this.getFetchEnum.bind(this);
    this.fetchSiteData = this.fetchSiteData.bind(this);
    this.siteBeforeSubmit = this.siteBeforeSubmit.bind(this);
    this.handleSelect = this.handleSelect.bind(this);
    this.gethandleSel = this.gethandleSel.bind(this);
    this.getFetchUser = this.getFetchUser.bind(this);
  }

  componentDidMount() {
    this.getFetchTaskDetail();
    this.getFetchUser();
  }

  getFetchTaskDetail(){
    fetch( `${Uniplatform.context.url}/dcci/task/manage/get/${this.props.page.id}` , {
    credentials: 'same-origin',
    method: 'POST'
    } )
      .then( ( res ) => res.json() )
      .then( ( sites ) => {
        let name = sites.data.name;
        let type = sites.data.type;
        this.setState({name,type});
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  getFetchUser(){
    fetch( `${Uniplatform.context.url}/common/user/userlist/crawler` , {
    credentials: 'same-origin',
    method: 'POST'
    } )
      .then( ( res ) => res.json() )
      .then( ( data ) => {
        let userList = [];
        if(data.length > 0){
          data.map((item,index) => {
            let users = {
              value : item.sysUserId.toString(),
              text : item.sysUserAccount
            };
            userList.push(users);
          });
        }
        this.setState({userList});
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  beforeSubmit(data){
    this.setState({ show : true });
    let manageId = this.props.page.id;
    let crawlerIds = [];
    let selectSite = this.state.selectSite ;
    crawlerIds = selectSite.map((item,index) => 
      item.value
    ).join(",");
    data.deadline = new Date(data.deadline).getTime();
    data["manageId"] = manageId;
    data["crawlerIds"] = crawlerIds;
    return data;
  }

  afterSubmit(data) {
    this.setState({ show : false });
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      window.open(`/dcci/platform/require/list`,'_self');
    }else{
      popup(<Dialog 
            title="错误信息" 
            message={data.message} 
          />);
    }
  }

  siteListData(){
    this.setState({ siteDataVis : true });
    this.getFetchEnum();
    this.fetchSiteData();
  }

  fetchSiteData(){
    let param = new FormData();
    let { size,type,index,ip } = this.state;
    param.append( 'size', size );
    param.append( 'number', index);
    param.append( 'type', type );
    param.append( 'ip', ip );
    fetch( `${Uniplatform.context.url}/dcci/crawler/search` , {
      credentials: 'same-origin',
      method: 'POST',
      body: param
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let siteDatas = apiData.data.content;
          let total = apiData.data.totalElements;
          this.setState({total});
          if (siteDatas.length <= 0){
            popup(<Snackbar message="抱歉，暂时没有数据"/>);
            this.setState({siteDatas});
          }else{
            this.gethandleSel(siteDatas);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  exChangePagi(index, size) {
    this.setState({index, size,}, () => this.fetchSiteData());
  }

  siteDataClose(){
    this.setState({ siteDataVis : false });
  }

  getFetchEnum(){
    fetch( `${Uniplatform.context.url}/dcci/task/manage/list/enum` , {
    credentials: 'same-origin',
    method: 'POST'
    } )
      .then( ( res ) => res.json() )
      .then( ( tasks ) => {
        let crawlerType = tasks.data.crawlertype;
        this.setState({crawlerType});
      }).catch( ( err ) => console.log( err.toString() ) );
  }
  
  getType(data,dataSource){
    let result = '';
    let { crawlerType } = this.state;
    if(dataSource == 'crawlerType'){
      crawlerType.map((item,indexType) => {
        for ( var index in item ) {
          if(index == data){
            result = item[data];
          }
        }
      });
      return result;
    }
  }

  siteBeforeSubmit(data){
    let ip = data.ip;
    let type = data.type;
    this.setState({ ip,type } , () => this.fetchSiteData() );
  }

  gethandleSel(siteDatas){
    let selectSite = this.state.selectSite;
    if(selectSite.length >0){
      selectSite.map((item,index) => {
        siteDatas.map((itemSite,indexSite) => {
          if(itemSite.id.toString() == item.value){
            siteDatas[indexSite]["checked"] = true;
          }
        });
      });
    }
    this.setState({siteDatas});
  }

  handleSelect(data,currentData){
    let selectSite = this.state.selectSite;
    const { siteDatas } = this.state;
    let flag = false;
    if(typeof(currentData) != "undefined"){
      selectSite.map((item,siteIndex) => {
        if(item.value == currentData.id.toString()){
          selectSite.splice(selectSite.indexOf(selectSite[siteIndex]),1);
          flag = true;
        }
      });
      if(!flag){
        let siteSel = {
          value : currentData.id.toString(),
          text : `${currentData.server.ip}:${currentData.port}`,
        };
        selectSite.push(siteSel);
      }
      
      for ( const ele of siteDatas ) {
        if(ele.id == currentData.id){
          ele.checked = true;
        }
      }
    }
    
    this.setState({ selectSite, siteDatas });
  }

  deleteSelSite(index){
    let selectSite = this.state.selectSite;
    const { siteDatas } = this.state;
    for ( const ele of siteDatas ) {
      if(ele.id.toString() == selectSite[index].value){
        ele.checked = false;
      }
    }
    selectSite.splice(selectSite.indexOf(selectSite[index]),1);
    this.setState({ selectSite,siteDatas });
  }

  render() {
    let {crawlerType,levelType,name,siteDataVis,index,size,total,siteDatas,selectSite,type,userList,show } = this.state;
    return (
        <Page>
            <SingleBanner prefix="数据中心" id={''}/>
            <Divider />
            <Row>
               <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                  <Com_Menu url={'/dcci/platform/require/list'} />
                </Col>
                <Col size={{ normal: 24, small: 24, medium: 12, large: 12 }} style={{minHeight:'600px'}}>

                  <NavigationBar code={'assignTask'} innerId={this.props.page.id}/>

                  <Form
                    method="post"
                    action={ `${contextUrl}/dcci/task/assign/save` }
                    type="horizontal"
                    async={ true }
                    enctype="multipart/form-data"
                    onSubmit={this.beforeSubmit}
                    onAfterSubmit={this.afterSubmit}
                  >
                    <FormItem>
                      <Label>任务名:</Label>
                      <Input type="text" value={name} readonly />
                    </FormItem>
                    <FormItem required>
                      <Label>截至日期:</Label>
                      <DateTimePicker placeholder="年 - 月 - 日" name="deadline" showTime={ true } showOk/>
                    </FormItem>
                    <FormItem required>
                      <Label>任务分配:</Label>
                      <Select value={ 'TOTAL' } name="assignType">
                        <Option value='TOTAL' >单一分配</Option>
                        <Option value='AVERAGE' >平均分配</Option>
                      </Select>
                    </FormItem>
                    <FormItem required>
                      <Label>采集器:</Label>
                      {
                        selectSite.length > 0 ?
                        <div className="epm field">
                          <div className="epm dropdown multiple select">
                            <div className="trigger">
                              <div className="pills">
                                {
                                  selectSite.map((item,index) => {
                                    return(<span className="pill" key={index}><span className="text">{item.text}</span><span className="close" onClick={this.deleteSelSite.bind(this,index)}><i className="fa fa-times"></i></span></span>)
                                  })
                                }
                              </div>
                            </div>
                          </div>
                        </div>
                        : ''
                      }
                      <a onClick={this.siteListData.bind(this)} style={{display:'inline-block'}}>采集器列表</a>
                    </FormItem>
                     <FormItem required>
                      <Label>采集员:</Label>
                      <Select name="userIds" search={ true } multiple={ true } dataSource={userList} />
                    </FormItem>
                    <Button type="success" active={false} htmlType="submit">提交</Button>
                  </Form>
                </Col>  
                { 
                  show ?
                  <div className="table-native-tableLoading" style={{left:0}}>
                    <div style={{position:'absolute',top:'55%',left:'47%'}}>正在分配,请稍等...</div>
                    <Loading />
                  </div>  
                  : ''
                }  
              </Col>
            </Row>     
            <Footer />

            <Modal visible={siteDataVis} onClose={this.siteDataClose.bind(this)} size="large">
            <ModalHeader>
              采集器列表
            </ModalHeader>
            <ModalBody>

              <Form type="inline"
                        action="fack" onSubmit={ this.siteBeforeSubmit }>
                <FormItem>
                  <Label>ip:</Label>
                  <Input name="ip" placeholder="请输入服务器地址"/>
                </FormItem>
                <FormItem>
                  <Label>采集类型:</Label>
                  <Select value={ type } name="type">
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

              <Table dataSource={siteDatas} striped={ true } checkable onCheck={ this.handleSelect } className="disCheck" complex headBolder={ true } >
                  <Column dataIndex="name" title="名称" textAlign="center"/>
                  <Column title="服务器" textAlign="center">
                 { ( value ) => {
                    let result = value.server.ip;
                    return result;
                  } }
                  </Column>
                  <Column dataIndex="port" title="端口" textAlign="center" />
                  <Column title="采集类型" textAlign="center">
                      { ( value ) => {
                          let result = this.getType(value.type,'crawlerType');
                          return result;
                      } }
                    </Column>
                  <Column dataIndex="maxSiteNum" title="最大采集点" textAlign="center"/>
                  <Column dataIndex="siteNum" title="采集点" textAlign="center"/>
                </Table>
                <Divider/>
                <Pagination align='right' index={index} total={total} size={size} onChange={this.exChangePagi.bind(this)}/>
            </ModalBody>
            <ModalFooter>
              <Button onClick={this.siteDataClose.bind(this)}>确定</Button>
            </ModalFooter>
          </Modal>


        </Page>
    );
  }

}
AssignTask.UIPage = page;
export default AssignTask;