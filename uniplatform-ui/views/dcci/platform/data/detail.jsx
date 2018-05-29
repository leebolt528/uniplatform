import React, { Component } from 'react';
import {Page,Row,Col,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Upload,Textarea,Dialog,Text,Table,Column,Pagination,Modal, ModalHeader, ModalBody, ModalFooter} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '商城详情',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

const contextUrl = '/uniplatform';

class ApiDetail extends Component {
  constructor(props){
    super(props);
    this.state = {
      index:1,
      size:20,
      total:0,
      name : '',
      url: '',
      method : '',
      apiDetail : [],
      singleApi :[],
      apiAddVis: false,
      apiEditorVis : false
    }
    this.getFetchApiDetail = this.getFetchApiDetail.bind(this);
    this.beforeSubmit = this.beforeSubmit.bind(this);
    this.afterApiAddSub = this.afterApiAddSub.bind(this);
    this.beforeApiAddSub = this.beforeApiAddSub.bind(this);
    this.formTirggerApiAdd = this.formTirggerApiAdd.bind(this);
    this.beforeApiUpdateSub = this.beforeApiUpdateSub.bind(this);
    this.afterApiUpdateSub = this.afterApiUpdateSub.bind(this);
  }

  componentDidMount() {
    this.getFetchApiDetail();
  }

  getFetchApiDetail(){
    let param = new FormData();
    let { size,name,url,index,method } = this.state;
    param.append( 'apiId', this.props.page.apiId );
    param.append( 'size', size );
    param.append( 'name', name );
    param.append( 'number', index);
    param.append( 'method', method );
    param.append( 'url', url );
    fetch( `${Uniplatform.context.url}/dcci/api/detail/list` , {
      credentials: 'same-origin',
      method: 'POST',
      body: param
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let apiDetail = apiData.data.content;
          let total = apiData.data.totalElements;
          this.setState({apiDetail,total});
          if (apiDetail.length <= 0){
            popup(<Snackbar message="抱歉，暂时没有数据"/>);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  exChangePagi(index, size) {
    this.setState({index, size}, () => this.getFetchApiDetail());
  }

  beforeSubmit(data){
    let url = data.url;
    let name = data.name;
    let method = data.method;
    this.setState({ url,name,method } , () => this.getFetchApiDetail() );
  }

  addApiDetail(){
    this.setState({apiAddVis: true});
  }

  apiAddClose(){
    this.apiAddreset();
    this.setState({apiAddVis: false});
  }

   formTirggerApiAdd( trigger ) {
    this.apiAddreset = trigger.reset;
  }

  beforeApiAddSub(data){
    data["apiId"] = this.props.page.apiId;
    return data;
  }

  afterApiAddSub(data){
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.getFetchApiDetail();
      this.apiAddClose();
    }else{
      this.showError(data);
    }
  }

  showError(data){
    popup(<Dialog 
          title="错误信息" 
          message={data.message} 
        />);
  }

  apiUpdateClose(){
    this.setState({apiEditorVis : false});
  }

  beforeApiUpdateSub(data) {
    data["id"] = this.state.singleApi.id;
    return data;
  }

  afterApiUpdateSub(data){
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.getFetchApiDetail();
      this.apiUpdateClose();
    }else{
      this.showError(data);
    }
  }

  getEditor(id){
    let apiEditorVis = true;
    fetch( `${Uniplatform.context.url}/dcci/api/detail/get/${id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let singleApi = apiData.data;
          this.setState({singleApi , apiEditorVis});
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  getExample(id){
    window.open(`/dcci/platform/data/example?id=${id}&apiId=${this.props.page.apiId}`,'_self');
  }

  apiDeleteData(value){
    const confirmShow = confirm("确定要删除该条数据?");
    if (!confirmShow) {
      return false;
    }

    fetch( `${Uniplatform.context.url}/dcci/api/detail/delete/${value.id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( data ) => {
          if(data.code == '1'){
            popup(<Snackbar message={data.message}/>);
            this.getFetchApiDetail();
          }else{
            this.showError(data);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  render() {
    let {apiDetail,index,size,total,apiAddVis,apiEditorVis,singleApi} = this.state;
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

                <NavigationBar code={'apiDetail'} innerId={this.props.page.apiId}/>

                <Form type="inline" action="fack" onSubmit={ this.beforeSubmit }>
                  <FormItem>
                    <Label>名称:</Label>
                    <Input placeholder="请输入名称" name="name"/>
                  </FormItem>
                  <FormItem>
                    <Label>请求方式:</Label>
                    <Select value={ 'ALL' } name="method">
                      <Option value='ALL'>全部</Option>
                      <Option value='GET'>get</Option>
                      <Option value='POST'>post</Option>
                    </Select>
                  </FormItem>
                  <FormItem>
                    <Label>url:</Label>
                    <Input placeholder="请输入地址" name="url"/>
                  </FormItem>
                  <Button htmlType="submit" type="default">查询</Button>
                </Form>

                <ButtonGroup style={{position:'relative',zIndex:1}}>
                  <Button type="default" size="small" onClick={this.addApiDetail.bind(this)}>添加</Button>
                </ButtonGroup>
                <Divider/>
                
                <Table dataSource={apiDetail} striped={ true } complex headBolder={ true }>
                  <Column dataIndex="name" title="名称" textAlign="center" scaleWidth="16%"/>
                  <Column dataIndex="url" title="地址" textAlign="center" scaleWidth="44%"/>
                  <Column dataIndex="method" title="请求方式" textAlign="center" scaleWidth="10%"/>
                  <Column dataIndex="user" title="负责人" textAlign="center" scaleWidth="15%"/>
                  <Column title="操作" textAlign="center" scaleWidth="15%">
                  { ( value ) => { 
                      return (
                        <p className="complex-link">
                          <a onClick={this.getEditor.bind(this,value.id)}>修改</a>
                          <a onClick={this.apiDeleteData.bind(this,value)}>删除</a>
                          <a onClick={this.getExample.bind(this,value.id)}>示例</a>
                        </p>
                      )
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

          <Form type="horizontal"
            method="post"
            action={`${contextUrl}/dcci/api/detail/save`}
            async={true}
            trigger={ this.formTirggerApiAdd }
            onSubmit={this.beforeApiAddSub }
            onAfterSubmit={this.afterApiAddSub }
            >
          <Modal visible={apiAddVis} onClose={this.apiAddClose.bind(this)}>
            <ModalHeader>
              添加详情api
            </ModalHeader>
            <ModalBody>
              <FormItem required>
                <Label>名称</Label>
                <Input name="name" placeholder="请输入名称"/>
              </FormItem>
              <FormItem>
                <Label>请求方式</Label>
                <Select value={ 'GET' } name="method">
                  <Option value='GET'>get</Option>
                  <Option value='POST'>post</Option>
                </Select>
              </FormItem>
              <FormItem required>
                <Label>地址</Label>
                <Input name="url" placeholder="请输入地址" type="url"/>
              </FormItem>
              <FormItem required>
                <Label>示例</Label>
                <Input name="sample" placeholder="请输入示例" type="url"/>
              </FormItem>
              <FormItem required>
                <Label>负责人</Label>
                <Input name="user" placeholder="请输入姓名"/>
              </FormItem>
              <FormItem required>
                <Label>邮箱</Label>
                <Input placeholder="请输入邮箱地址" pattern= { /^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$/ } name="email"/>
              </FormItem>
              <FormItem required>
                <Label>电话</Label>
                <Input name="phone" placeholder="请输入电话" pattern= { /^1[34578]\d{9}$/ }/>
              </FormItem>
            </ModalBody>
            <ModalFooter>
              <Button onClick={this.apiAddClose.bind(this)}>关闭</Button>
              <Button type="primary" htmlType="submit">确定</Button>
            </ModalFooter>
          </Modal>
        </Form>

         <Form type="horizontal"
            method="post"
            action={`${contextUrl}/dcci/api/detail/update`}
            async={true}
            onSubmit={this.beforeApiUpdateSub }
            onAfterSubmit={this.afterApiUpdateSub }
            >
          <Modal visible={apiEditorVis} onClose={this.apiUpdateClose.bind(this)}>
            <ModalHeader>
              编辑详情api
            </ModalHeader>
            <ModalBody>
              <FormItem required>
                <Label>名称</Label>
                <Input name="name" value={singleApi ? singleApi.name : null}/>
              </FormItem>
              <FormItem>
                <Label>请求方式</Label>
                <Select value={singleApi ? singleApi.method : null} name="method">
                  <Option value='GET'>get</Option>
                  <Option value='POST'>post</Option>
                </Select>
              </FormItem>
              <FormItem required>
                <Label>地址</Label>
                <Input name="url" placeholder="请输入地址" type="url" value={singleApi ? singleApi.url : null}/>
              </FormItem>
              <FormItem required>
                <Label>示例</Label>
                <Input name="sample" placeholder="请输入示例" type="url" value={singleApi ? singleApi.sample : null}/>
              </FormItem>
              <FormItem required>
                <Label>负责人</Label>
                <Input name="user" placeholder="请输入姓名" value={singleApi ? singleApi.user : null}/>
              </FormItem>
              <FormItem required>
                <Label>邮箱</Label>
                <Input placeholder="请输入邮箱地址" pattern= { /^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$/ } name="email" value={singleApi ? singleApi.email : null}/>
              </FormItem>
              <FormItem required>
                <Label>电话</Label>
                <Input name="phone" placeholder="请输入电话" value={singleApi ? singleApi.phone : null} pattern= { /^1[34578]\d{9}$/ } />
              </FormItem>
            </ModalBody>
            <ModalFooter>
              <Button onClick={this.apiUpdateClose.bind(this)}>关闭</Button>
              <Button type="primary" htmlType="submit">确定</Button>
            </ModalFooter>
          </Modal>
        </Form>

      </Page>
    );
  }

}
ApiDetail.UIPage = page;
export default ApiDetail;