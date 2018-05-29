import React, { Component } from 'react';
import {Page,Row,Col,Divider,Button,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Upload,Textarea,Dialog,Text,Table,Column,Pagination,Modal, ModalHeader, ModalBody, ModalFooter} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '商城管理',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

const contextUrl = '/uniplatform';

class ApiList extends Component {
  constructor(props){
    super(props);
    this.state = {
      index:1,
      size:20,
      total:0,
      name : '',
      type: '',
      status : '',
      apiList : [],
      apiType : [],
      statusType : [],
      units : []
    }
    this.getFetchApiList = this.getFetchApiList.bind(this);
    this.getFetchApiMnum = this.getFetchApiMnum.bind(this);
    this.beforeSubmit = this.beforeSubmit.bind(this);
  }

  componentDidMount() {
    this.getFetchApiList();
    this.getFetchApiMnum();
  }

  getFetchApiList(){
    let param = new FormData();
    let { size,name,type,index,status } = this.state;
    param.append( 'size', size );
    param.append( 'name', name );
    param.append( 'number', index);
    param.append( 'status', status );
    param.append( 'type', type );
    fetch( `${Uniplatform.context.url}/dcci/api/list` , {
      credentials: 'same-origin',
      method: 'POST',
      body: param
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let apiList = apiData.data.content;
          let total = apiData.data.totalElements;
          this.setState({apiList,total});
          if (apiList.length <= 0){
            popup(<Snackbar message="抱歉，暂时没有数据"/>);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  getFetchApiMnum(){
    fetch( `${Uniplatform.context.url}/dcci/api/list/enum` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let apiType = apiData.data.apitype;
          let statusType = apiData.data.status;
          let units = apiData.data.unit;
          this.setState({apiType,statusType,units});
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  getType(data,dataSource){
    let result = '';
    let { apiType,statusType,units } = this.state;
    if(dataSource == 'type'){
      apiType.map((item,indexType) => {
        for ( var index in item ) {
          if(index == data){
            result = item[data];
          }
        }
      });
      return result;
    }else if(dataSource == 'status'){
       statusType.map((item,indexType) => {
        for ( var index in item ) {
          if(index == data){
            result = item[data];
          }
        }
      });
      return result;
    }else if(dataSource == 'unit'){
      let unit = data.unit;
      let specif = data.specifications;
      units.map((item,indexType) => {
        for ( var index in item ) {
          if(index == unit){
            result = `${specif} / ${item[unit]}`;
          }
        }
      });
      return result;
    }
  }
  
  exChangePagi(index, size) {
    this.setState({index, size}, () => this.getFetchApiList());
  }

  beforeSubmit(data){
    let status = data.status;
    let name = data.name;
    let type = data.type;
    this.setState({ status,name,type } , () => this.getFetchApiList() );
  }

  addApiData(){
    window.open(`/dcci/platform/data/add`,'_self');
  }

  showError(data){
    popup(<Dialog 
          title="错误信息" 
          message={data.message} 
        />);
  }

  apiDeleteData(value){
    const confirmShow = confirm("确定要删除该条数据?");
    if (!confirmShow) {
      return false;
    }

    fetch( `${Uniplatform.context.url}/dcci/api/delete/${value.id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( data ) => {
          if(data.code == '1'){
            popup(<Snackbar message={data.message}/>);
            this.getFetchApiList();
          }else{
            this.showError(data);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  apiStatusData(value,dataString){
    let confirmShow ;
    if(dataString == 'close'){
      confirmShow = confirm("确定要关闭该条数据?");
    }else if(dataString == 'open'){
      confirmShow = confirm("确定要开启该条数据?");
    }
    if (!confirmShow) {
      return false;
    }
    
    fetch( `${Uniplatform.context.url}/dcci/api/status/${value.id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( data ) => {
          if(data.code == '1'){
            popup(<Snackbar message={data.message}/>);
            this.getFetchApiList();
          }else{
            this.showError(data);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  apiDetail(value){
    window.open(`/dcci/platform/data/detail?apiId=${value.id}`,'_self');
  }

  getCode(){
    window.open(`/dcci/platform/data/codes`,'_self');
  }

  apiEditorData(value){
    window.open(`/dcci/platform/data/update?id=${value.id}`,'_self');
  }

  getOperate(value){
    let status = value.status;
    let result ;
    if(status == 'OPEN'){
      result = <p className="complex-link">
                  <a onClick={this.apiStatusData.bind(this,value,'close')}>关闭</a>
                  <a onClick={this.apiEditorData.bind(this,value)}>修改</a>
                  <a onClick={this.apiDeleteData.bind(this,value)}>删除</a>
                  <a onClick={this.apiDetail.bind(this,value)}>api详情</a>
                </p>;
    }else if(status == 'CLOSE'){
      result = <p className="complex-link">
                <a onClick={this.apiStatusData.bind(this,value,'open')}>开启</a>
                <a onClick={this.apiEditorData.bind(this,value)}>修改</a>
                <a onClick={this.apiDeleteData.bind(this,value)}>删除</a>
                <a onClick={this.apiDetail.bind(this,value)}>api详情</a>
              </p>;
    }
    return result;
  }

  render() {
    let {apiList,index,size,total,apiType,statusType,units} = this.state;
    return (
        <Page>
          <SingleBanner prefix="数据中心" id={''}/>
          <Divider />
          <Row>
              <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
              <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                <Com_Menu url={'/dcci/platform/data/list'} />
              </Col>
              <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} style={{minHeight:'600px'}}>

                <NavigationBar code={'apiList'} />

                <Form type="inline" action="fack" onSubmit={ this.beforeSubmit }>
                  <FormItem>
                    <Label>名称:</Label>
                    <Input placeholder="请输入名称" name="name"/>
                  </FormItem>
                  <FormItem>
                    <Label>数据分类:</Label>
                    <Select value={ 'ALL' } name="type">
                    <Option value='ALL'>全部</Option>
                    {
                      apiType.map((item,indexType) => {
                        for ( var index in item ) {
                          return (<Option key={index} value={index}>{item[index]}</Option>)
                        }
                      })
                    }
                    </Select>
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
                  <Button htmlType="submit" type="default">查询</Button>
                </Form>

                <ButtonGroup style={{position:'relative',zIndex:1}}>
                  <Button type="default" size="small" onClick={this.addApiData.bind(this)}>添加</Button>
                  <Button type="default" size="small" onClick={this.getCode.bind(this)}>状态码</Button>
                </ButtonGroup>
                <Divider/>
                
                <Table dataSource={apiList} striped={ true } complex headBolder={ true }>
                  <Column dataIndex="name" title="名称" textAlign="center" scaleWidth="18%"/>
                  <Column title="数据分类" textAlign="center" scaleWidth="15%">
                  { ( value ) => {
                    let result = this.getType(value.type,'type');
                    return result;
                  } }
                  </Column>
                  <Column dataIndex="price" title="价格" textAlign="center" scaleWidth="10%"/>
                  <Column title="规格 / 单位" textAlign="center" scaleWidth="15%">
                  { ( value ) => {
                    let result = this.getType(value,'unit');
                    return result;
                  } }
                  </Column>
                  <Column dataIndex="count" title="调用数" textAlign="center" scaleWidth="10%"/>
                  <Column title="状态" textAlign="center" scaleWidth="10%">
                  { ( value ) => {
                    let result = this.getType(value.status,'status');
                    return result;
                  } }
                  </Column>
                  <Column title="操作" textAlign="center" scaleWidth="22%">
                  { ( value ) => { 
                      let result = this.getOperate(value); 
                      return result;
                      }    
                    }
                  </Column>
                </Table>
                <Divider/>
                <Pagination align='right' index={index} total={total} size={size} onChange={this.exChangePagi.bind(this)}/>
                <Divider/>
              </Col>    
            </Col>
          </Row>     
          <Footer />
      </Page>
    );
  }

}
ApiList.UIPage = page;
export default ApiList;