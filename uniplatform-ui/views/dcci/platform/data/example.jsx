import React, { Component } from 'react';
import {Page,Row,Col,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Upload,Textarea,Dialog,Tabs,Tab,Table,Column,Pagination,Modal,ModalHeader,ModalBody,ModalFooter} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: 'api示例',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ],
    js: [
        '../../js/ace.min.js',
        '../../js/mode-json.min.js'
    ]
};

const contextUrl = '/uniplatform';

class ApiExample extends Component {
  constructor(props){
    super(props);
    this.state = {
      detailId : this.props.page.id,
      requestIndex:1,
      requestSize : 20,
      requestTotal:0,
      requestList : [],
      requestAddVis : false,
      requestUpdateVis : false,
      requestUpdate : [],

      returnIndex:1,
      returnSize : 20,
      returnTotal:0,
      returnList : [],
      returnAddVis : false,
      returnUpdateVis : false,
      returnUpdate : [],
      getExample : [],
      backExample : []
    }
    this.fetchRequestList = this.fetchRequestList.bind(this);
    this.formTirggerRequestAdd = this.formTirggerRequestAdd.bind(this);
    this.beforeRequestAddSub = this.beforeRequestAddSub.bind(this);
    this.afterRequestAddSub = this.afterRequestAddSub.bind(this);
    this.beforeRequestUpdateSub = this.beforeRequestUpdateSub.bind(this);
    this.afterRequestUpdateSub = this.afterRequestUpdateSub.bind(this);

    this.fetchReturnList = this.fetchReturnList.bind(this);
    this.formTirggerReturnAdd = this.formTirggerReturnAdd.bind(this);
    this.beforeReturnAddSub = this.beforeReturnAddSub.bind(this);
    this.afterReturnAddSub = this.afterReturnAddSub.bind(this);
    this.beforeReturnUpdateSub = this.beforeReturnUpdateSub.bind(this);
    this.afterReturnUpdateSub = this.afterReturnUpdateSub.bind(this);

    this.beforeGetExampleSub = this.beforeGetExampleSub.bind(this);
    this.afterGetExampleSub = this.afterGetExampleSub.bind(this);

    this.beforeBackExampleSub = this.beforeBackExampleSub.bind(this);
    this.afterBackExampleSub = this.afterBackExampleSub.bind(this);
  }

  componentDidMount() {
    this.fetchRequestList();
  }
  showError(data){
    popup(<Dialog 
          title="错误信息" 
          message={data.message} 
        />);
  }
  showDelete(){
    const confirmShow = confirm("确定要删除该条数据?");
    if (!confirmShow) {
      return false;
    }
  }
  //-----请求参数
  fetchRequestList(){
    let param = new FormData();
    let { requestSize,requestIndex,detailId } = this.state;
    param.append( 'size', requestSize );
    param.append( 'number', requestIndex);
    param.append( 'detailId', detailId );
    fetch( `${Uniplatform.context.url}/dcci/api/request/param/list` , {
      credentials: 'same-origin',
      method: 'POST',
      body: param
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let requestList = apiData.data.content;
          let requestTotal = apiData.data.totalElements;
          this.setState({requestList,requestTotal});
          if (requestList.length <= 0){
            popup(<Snackbar message="抱歉，暂时没有数据"/>);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }
  requestChangePage(requestIndex, requestSize){
    this.setState({requestIndex, requestSize}, () => this.fetchRequestList());
  }
  addRequest(){
    this.setState({ requestAddVis : true });
  }
  formTirggerRequestAdd( trigger ) {
    this.requestreset = trigger.reset;
  }
  requestAddClose(){
    this.requestreset();
    this.setState({ requestAddVis : false });
  }
  beforeRequestAddSub(data){
    data["detailId"] = this.state.detailId;
    return data;
  }
  afterRequestAddSub(data){
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.fetchRequestList();
      this.requestAddClose();
    }else{
      this.showError(data);
    }
  }
  requestUpdate(id){
    this.setState({ requestUpdateVis : true});
    fetch( `${Uniplatform.context.url}/dcci/api/request/param/get/${id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let requestUpdate = apiData.data;
          this.setState({requestUpdate});
        }).catch( ( err ) => console.log( err.toString() ) );
  }
  requestUpdateClose(){
    this.setState({ requestUpdateVis : false});
  }
  beforeRequestUpdateSub(data){
    data["id"] = this.state.requestUpdate.id;
    return data;
  }
  afterRequestUpdateSub(data){
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.fetchRequestList();
      this.requestUpdateClose();
    }else{
      this.showError(data);
    }
  }
  requestDlete(id){
    this.showDelete();
    fetch( `${Uniplatform.context.url}/dcci/api/request/param/delete/${id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( data ) => {
          if(data.code == '1'){
            popup(<Snackbar message={data.message}/>);
            this.fetchRequestList();
          }else{
            this.showError(data);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  //-----返回参数
  fetchReturnList(){
    let param = new FormData();
    let { returnIndex,returnSize,detailId } = this.state;
    param.append( 'size', returnSize );
    param.append( 'number', returnIndex);
    param.append( 'detailId', detailId );
    fetch( `${Uniplatform.context.url}/dcci/api/return/param/list` , {
      credentials: 'same-origin',
      method: 'POST',
      body: param
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let returnList = apiData.data.content;
          let returnTotal = apiData.data.totalElements;
          this.setState({returnList,returnTotal});
          if (returnList.length <= 0){
            popup(<Snackbar message="抱歉，暂时没有数据"/>);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }
  returnChangePage(returnIndex,returnSize){
    this.setState({returnIndex,returnSize}, () => this.fetchReturnList());
  }
  addReturn(){
    this.setState({ returnAddVis : true });
  }
  formTirggerReturnAdd( trigger ) {
    this.returnreset = trigger.reset;
  }
  returnAddClose(){
    this.returnreset();
    this.setState({ returnAddVis : false });
  }
  beforeReturnAddSub(data){
    data["detailId"] = this.state.detailId;
    return data;
  }
  afterReturnAddSub(data){
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.fetchReturnList();
      this.returnAddClose();
    }else{
      this.showError(data);
    }
  }
  returnUpdate(id){
    this.setState({ returnUpdateVis : true});
    fetch( `${Uniplatform.context.url}/dcci/api/return/param/get/${id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let returnUpdate = apiData.data;
          this.setState({returnUpdate});
        }).catch( ( err ) => console.log( err.toString() ) );
  }
  returnUpdateClose(){
    this.setState({ returnUpdateVis : false});
  }
  beforeReturnUpdateSub(data){
    data["id"] = this.state.returnUpdate.id;
    return data;
  }
  afterReturnUpdateSub(data){
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.fetchReturnList();
      this.returnUpdateClose();
    }else{
      this.showError(data);
    }
  }
  returnDlete(id){
    this.showDelete();
    fetch( `${Uniplatform.context.url}/dcci/api/return/param/delete/${id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( data ) => {
          if(data.code == '1'){
            popup(<Snackbar message={data.message}/>);
            this.fetchReturnList();
          }else{
            this.showError(data);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  //请求示例
  getFetchGiveExample(){
    let param = new FormData();
    let detailId = this.state.detailId;
    param.append( 'language', "JAVA");
    param.append( 'detailId', detailId );
    fetch( `${Uniplatform.context.url}/dcci/api/request/example/get` , {
      credentials: 'same-origin',
      method: 'POST',
      body : param
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let getExample = apiData.data;
          this.setState({ getExample });
          if (getExample == null){
            popup(<Snackbar message="抱歉，暂时没有数据"/>);
          }else{
            this.setState({ getExample } , () => this.editGetExample());
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }
  editGetExample(){
    //初始化对象
    let getExample = this.state.getExample;
    if(getExample.hasOwnProperty("code")){
      getExample = getExample.code;
    }
    let editor = ace.edit("getExampleDiv");
    //设置语言
    let language = "json";
    editor.session.setMode("ace/mode/" + language);
    //设置编辑器中的值
    editor.setValue(getExample);
    //字体大小
    editor.setFontSize(18);
    //设置只读（true时只读，用于展示代码）
    editor.setReadOnly(false);
    //自动换行,设置为off关闭
    editor.setOption("wrap", "free");
  }
  beforeGetExampleSub(data){
    let getExample = this.state.getExample;
    if(getExample != null){
      data["id"] = this.state.getExample.id;
    }else{
       data["detailId"] = this.state.detailId;
    }
    data["language"] = "JAVA";
    data.code = ace.edit("getExampleDiv").getValue();
    return data;
  }
  afterGetExampleSub(data){
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.getFetchGiveExample();
    }else{
      this.showError(data);
    }
  }

  //返回示例
  getFetchBackExample(){
    let param = new FormData();
    let detailId = this.state.detailId;
    param.append( 'detailId', detailId );
    fetch( `${Uniplatform.context.url}/dcci/api/return/example/get` , {
      credentials: 'same-origin',
      method: 'POST',
      body : param
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let backExample = apiData.data;
          this.setState({ backExample });
          if (backExample == null){
            popup(<Snackbar message="抱歉，暂时没有数据"/>);
          }else{
            this.setState({ backExample } , () => this.editBackExample());
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }
  editBackExample(){
    //初始化对象
    let backExample = this.state.backExample;
    if(backExample.hasOwnProperty("example")){
      backExample = backExample.example;
    }
    let editor = ace.edit("backExampleDiv");
    //设置语言
    let language = "json";
    editor.session.setMode("ace/mode/" + language);
    //设置编辑器中的值
    editor.setValue(backExample);
    //字体大小
    editor.setFontSize(18);
    //设置只读（true时只读，用于展示代码）
    editor.setReadOnly(false);
    //自动换行,设置为off关闭
    editor.setOption("wrap", "free");
  }
  beforeBackExampleSub(data){
    let backExample = this.state.backExample;
    if(backExample != null){
      data["id"] = this.state.backExample.id;
    }else{
       data["detailId"] = this.state.detailId;
    }
    data.example = ace.edit("backExampleDiv").getValue();
    return data;
  }
  afterBackExampleSub(data){
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.getFetchBackExample();
    }else{
      this.showError(data);
    }
  }

  render() {
    let { requestIndex,requestSize,requestTotal,requestList,requestAddVis,requestUpdateVis,requestUpdate,returnIndex,returnSize,returnTotal,returnList,returnAddVis,returnUpdateVis,returnUpdate } = this.state;
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
              <NavigationBar code={'apiExample'} id={this.state.detailId} innerId={this.props.page.apiId}/>
              <Tabs>
                <Tab title="请求参数">
                  <ButtonGroup style={{position:'relative',zIndex:1}}>
                    <Button type="default" size="small" onClick={this.addRequest.bind(this)}>添加</Button>
                  </ButtonGroup>
                  <Divider/>
                  <Table dataSource={requestList} striped={ true } complex headBolder={ true }>
                    <Column dataIndex="name" title="名称" textAlign="center" scaleWidth="15%"/>
                    <Column dataIndex="type" title="类型" textAlign="center" scaleWidth="15%"/>
                    <Column title="必填" textAlign="center" scaleWidth="10%">
                    { ( value ) => { 
                      return (value.require == '0' ? '否' : '是')
                      }    
                    }
                    </Column>
                    <Column dataIndex="example" title="示例" textAlign="center" scaleWidth="20%"/>
                    <Column dataIndex="description" title="描述" textAlign="center" scaleWidth="25%"/>
                    <Column title="操作" textAlign="center" scaleWidth="15%">
                    { ( value ) => { 
                        return (
                          <p className="complex-link">
                            <a onClick={this.requestUpdate.bind(this,value.id)}>修改</a>
                            <a onClick={this.requestDlete.bind(this,value.id)}>删除</a>
                          </p>
                        )
                        }    
                      }
                  </Column>
                  </Table>
                  <Divider/>
                  <Pagination align='right' index={requestIndex} total={requestTotal} size={requestSize} onChange={this.requestChangePage.bind(this)}/>
                  <Divider/>
                  <Form type="horizontal"
                      method="post"
                      action={`${contextUrl}/dcci/api/request/param/save`}
                      async={true}
                      trigger={ this.formTirggerRequestAdd }
                      onSubmit={this.beforeRequestAddSub }
                      onAfterSubmit={this.afterRequestAddSub }
                      >
                    <Modal visible={requestAddVis} onClose={this.requestAddClose.bind(this)}>
                      <ModalHeader>
                        添加请求参数
                      </ModalHeader>
                      <ModalBody>
                        <FormItem required>
                          <Label>名称</Label>
                          <Input name="name" placeholder="请输入名称"/>
                        </FormItem>
                        <FormItem>
                          <Label>类型</Label>
                          <Select value={ 'string' } name="type">
                            <Option value='string'>string</Option>
                            <Option value='int'>int</Option>
                            <Option value='boolean'>boolean</Option>
                          </Select>
                        </FormItem>
                        <FormItem>
                          <Label>必填</Label>
                          <Select value={ '1' } name="require">
                            <Option value='0'>否</Option>
                            <Option value='1'>是</Option>
                          </Select>
                        </FormItem>
                        <FormItem required>
                          <Label>示例</Label>
                          <Input name="example" placeholder="请输入示例"/>
                        </FormItem>
                        <FormItem>
                          <Label>描述</Label>
                          <Textarea name="description"  rows={ 3 } />
                        </FormItem>
                      </ModalBody>
                      <ModalFooter>
                        <Button onClick={this.requestAddClose.bind(this)}>关闭</Button>
                        <Button type="primary" htmlType="submit">确定</Button>
                      </ModalFooter>
                    </Modal>
                  </Form>
                  <Form type="horizontal"
                    method="post"
                    action={`${contextUrl}/dcci/api/request/param/update`}
                    async={true}
                    onSubmit={this.beforeRequestUpdateSub }
                    onAfterSubmit={this.afterRequestUpdateSub }
                    >
                    <Modal visible={requestUpdateVis} onClose={this.requestUpdateClose.bind(this)}>
                      <ModalHeader>
                        编辑请求参数
                      </ModalHeader>
                      <ModalBody>
                        <FormItem required>
                          <Label>名称</Label>
                          <Input name="name" value={requestUpdate ? requestUpdate.name : null }/>
                        </FormItem>
                        <FormItem>
                          <Label>类型</Label>
                          <Select name="type" value={requestUpdate ? requestUpdate.type : null }>
                            <Option value='string'>string</Option>
                            <Option value='int'>int</Option>
                            <Option value='boolean'>boolean</Option>
                          </Select>
                        </FormItem>
                        <FormItem>
                          <Label>必填</Label>
                          <Select name="require" value={requestUpdate ? requestUpdate.require + "" : null }>
                            <Option value='0'>否</Option>
                            <Option value='1'>是</Option>
                          </Select>
                        </FormItem>
                        <FormItem required>
                          <Label>示例</Label>
                          <Input name="example" value={requestUpdate ? requestUpdate.example : null }/>
                        </FormItem>
                        <FormItem>
                          <Label>描述</Label>
                          <Textarea name="description"  rows={ 3 }  value={requestUpdate ? requestUpdate.description : null }/>
                        </FormItem>
                      </ModalBody>
                      <ModalFooter>
                        <Button onClick={this.requestUpdateClose.bind(this)}>关闭</Button>
                        <Button type="primary" htmlType="submit">确定</Button>
                      </ModalFooter>
                    </Modal>
                  </Form>
                </Tab>
                <Tab title="返回参数" onClick={this.fetchReturnList.bind(this)} >
                   <ButtonGroup style={{position:'relative',zIndex:1}}>
                    <Button type="default" size="small" onClick={this.addReturn.bind(this)}>添加</Button>
                  </ButtonGroup>
                  <Divider/>
                  <Table dataSource={returnList} striped={ true } complex headBolder={ true }>
                    <Column dataIndex="name" title="名称" textAlign="center" scaleWidth="20%"/>
                    <Column dataIndex="type" title="类型" textAlign="center" scaleWidth="20%"/>
                    <Column dataIndex="example" title="示例" textAlign="center" scaleWidth="20%"/>
                    <Column dataIndex="description" title="描述" textAlign="center" scaleWidth="25%"/>
                    <Column title="操作" textAlign="center" scaleWidth="15%">
                    { ( value ) => { 
                        return (
                          <p className="complex-link">
                            <a onClick={this.returnUpdate.bind(this,value.id)}>修改</a>
                            <a onClick={this.returnDlete.bind(this,value.id)}>删除</a>
                          </p>
                        )
                        }    
                      }
                  </Column>
                  </Table>
                  <Divider/>
                  <Pagination align='right' index={returnIndex} total={returnTotal} size={returnSize} onChange={this.returnChangePage.bind(this)}/>
                  <Divider/>
                  <Form type="horizontal"
                      method="post"
                      action={`${contextUrl}/dcci/api/return/param/save`}
                      async={true}
                      trigger={ this.formTirggerReturnAdd }
                      onSubmit={this.beforeReturnAddSub }
                      onAfterSubmit={this.afterReturnAddSub }
                      >
                    <Modal visible={returnAddVis} onClose={this.returnAddClose.bind(this)}>
                      <ModalHeader>
                        添加请求参数
                      </ModalHeader>
                      <ModalBody>
                        <FormItem required>
                          <Label>名称</Label>
                          <Input name="name" placeholder="请输入名称"/>
                        </FormItem>
                        <FormItem>
                          <Label>类型</Label>
                          <Select value={ 'string' } name="type">
                            <Option value='string'>string</Option>
                            <Option value='int'>int</Option>
                            <Option value='boolean'>boolean</Option>
                          </Select>
                        </FormItem>
                        <FormItem required>
                          <Label>示例</Label>
                          <Input name="example" placeholder="请输入示例"/>
                        </FormItem>
                        <FormItem>
                          <Label>描述</Label>
                          <Textarea name="description"  rows={ 3 } />
                        </FormItem>
                      </ModalBody>
                      <ModalFooter>
                        <Button onClick={this.returnAddClose.bind(this)}>关闭</Button>
                        <Button type="primary" htmlType="submit">确定</Button>
                      </ModalFooter>
                    </Modal>
                  </Form>
                  <Form type="horizontal"
                    method="post"
                    action={`${contextUrl}/dcci/api/return/param/update`}
                    async={true}
                    onSubmit={this.beforeReturnUpdateSub }
                    onAfterSubmit={this.afterReturnUpdateSub }
                    >
                    <Modal visible={returnUpdateVis} onClose={this.returnUpdateClose.bind(this)}>
                      <ModalHeader>
                        编辑返回参数
                      </ModalHeader>
                      <ModalBody>
                        <FormItem required>
                          <Label>名称</Label>
                          <Input name="name" value={returnUpdate ? returnUpdate.name : null }/>
                        </FormItem>
                        <FormItem>
                          <Label>类型</Label>
                          <Select name="type" value={returnUpdate ? returnUpdate.type : null }>
                            <Option value='string'>string</Option>
                            <Option value='int'>int</Option>
                            <Option value='boolean'>boolean</Option>
                          </Select>
                        </FormItem>
                        <FormItem required>
                          <Label>示例</Label>
                          <Input name="example" value={returnUpdate ? returnUpdate.example : null }/>
                        </FormItem>
                        <FormItem>
                          <Label>描述</Label>
                          <Textarea name="description"  rows={ 3 }  value={returnUpdate ? returnUpdate.description : null }/>
                        </FormItem>
                      </ModalBody>
                      <ModalFooter>
                        <Button onClick={this.returnUpdateClose.bind(this)}>关闭</Button>
                        <Button type="primary" htmlType="submit">确定</Button>
                      </ModalFooter>
                    </Modal>
                  </Form>
                </Tab>
                <Tab title="请求示例" onClick={this.getFetchGiveExample.bind(this)}>
                  <Form type="horizontal"
                    method="post"
                    action={`${contextUrl}/dcci/api/request/example/addorupd`}
                    async={true}
                    onSubmit={this.beforeGetExampleSub }
                    onAfterSubmit={this.afterGetExampleSub }
                  >
                    <Button type="success" htmlType="submit" size="small" style={{ float:'left',marginBottom: '10px'}} >保存</Button>
                    <FormItem style={{textAlign:'left'}}>
                      <div id="getExampleDiv" style={{minHeight:'500px'}}>
                        <Textarea name="code" rows={25}></Textarea>
                      </div>
                    </FormItem>
                  </Form>
                </Tab>
                <Tab title="返回示例" onClick={this.getFetchBackExample.bind(this)}>
                  <Form type="horizontal"
                    method="post"
                    action={`${contextUrl}/dcci/api/return/example/addorupd`}
                    async={true}
                    onSubmit={this.beforeBackExampleSub }
                    onAfterSubmit={this.afterBackExampleSub }
                  >
                    <Button type="success" htmlType="submit" size="small" style={{float:'left',marginBottom: '10px'}} >保存</Button>
                    <FormItem style={{textAlign:'left'}}>
                      <div id="backExampleDiv" style={{minHeight:'500px'}}>
                        <Textarea name="example" rows={25}></Textarea>
                      </div>
                    </FormItem>
                  </Form>
                </Tab>
              </Tabs>
            </Col>
          </Col>
        </Row>
        <Footer />
      </Page>
    );
  }

}
ApiExample.UIPage = page;
export default ApiExample;