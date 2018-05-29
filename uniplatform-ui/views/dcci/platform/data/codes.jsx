import React, { Component } from 'react';
import {Page,Row,Col,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Upload,Textarea,Dialog,Text,Table,Column,Pagination,Modal, ModalHeader, ModalBody, ModalFooter} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '状态码',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

const contextUrl = '/uniplatform';

class CodeList extends Component {
  constructor(props){
    super(props);
    this.state = {
      index:1,
      size:20,
      total:0,
      codeList : [],
      codeAddVis : false
    }
    this.getFetchCodeList = this.getFetchCodeList.bind(this);
    this.formTirggerCodeAdd = this.formTirggerCodeAdd.bind(this);
    this.afterCodeAddSub = this.afterCodeAddSub.bind(this);
    this.handleEdit = this.handleEdit.bind( this );
  }

  componentDidMount() {
    this.getFetchCodeList();
  }

  getFetchCodeList(){
    let param = new FormData();
    let { size,index } = this.state;
    param.append( 'size', size );
    param.append( 'number', index);
    fetch( `${Uniplatform.context.url}/dcci/api/code/list` , {
      credentials: 'same-origin',
      method: 'POST',
      body: param
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let codeList = apiData.data.content;
          let total = apiData.data.totalElements;
          this.setState({codeList,total});
          if (codeList.length <= 0){
            popup(<Snackbar message="抱歉，暂时没有数据"/>);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  exChangePagi(index, size) {
    this.setState({index, size}, () => this.getFetchCodeList());
  }

  addCode(){
    this.setState({ codeAddVis : true });
  }

  formTirggerCodeAdd(trigger){
    this.codeAddreset = trigger.reset;
  }

  codeAddClose(){
    this.codeAddreset();
    this.setState({ codeAddVis : false });
  }

  afterCodeAddSub(data){
     if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      this.getFetchCodeList();
      this.codeAddClose();
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

  handleEdit( rowData, tableData ) {
    let param = new FormData();
    param.append( 'code', rowData.code );
    param.append( 'explain', rowData.explain);
    param.append( 'id', rowData.id);
    fetch( `${Uniplatform.context.url}/dcci/api/code/update` , {
      credentials: 'same-origin',
      method: 'POST',
      body: param
      } )
        .then( ( res ) => res.json() )
        .then( ( data ) => {
          if(data.code == '1'){
            popup(<Snackbar message={data.message}/>);
            this.getFetchCodeList();
          }else{
            this.showError(data);
          }   
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  deleteCode(id){
    const confirmShow = confirm("确定要删除该条数据?");
    if (!confirmShow) {
      return false;
    }

    fetch( `${Uniplatform.context.url}/dcci/api/code/delete/${id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( data ) => {
          if(data.code == '1'){
            popup(<Snackbar message={data.message}/>);
            this.getFetchCodeList();
          }else{
            this.showError(data);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  render() {
    let {codeList,index,size,total,codeAddVis} = this.state;
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

                <NavigationBar code={'codeList'}/>

                <ButtonGroup style={{position:'relative',zIndex:1}}>
                  <Button type="default" size="small" onClick={this.addCode.bind(this)}>添加</Button>
                </ButtonGroup>
                <Divider/>
                
                <Table dataSource={codeList} striped={ true } onAfterEdit={ this.handleEdit }  complex headBolder={ true }>
                  <Column dataIndex="code" title="状态码" textAlign="center" editable={ true } scaleWidth="18%"/>
                  <Column dataIndex="explain" title="状态码说明" textAlign="center" editable={ true } editComponent={ <Textarea /> } scaleWidth="72%" />
                  <Column title="操作" textAlign="center" scaleWidth="10%">
                  { ( value ) => { 
                      return ( <p>
                        <a onClick={this.deleteCode.bind(this,value.id)}>删除</a>
                      </p>)
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
            action={`${contextUrl}/dcci/api/code/save`}
            async={true}
            trigger={ this.formTirggerCodeAdd }
            onAfterSubmit={this.afterCodeAddSub }
            >
          <Modal visible={codeAddVis} onClose={this.codeAddClose.bind(this)}>
            <ModalHeader>
              添加状态码
            </ModalHeader>
            <ModalBody>
              <FormItem required>
                <Label>状态码</Label>
                <Input name="code" placeholder="请输入名称"/>
              </FormItem>
              <FormItem required>
                <Label>状态码说明</Label>
                <Textarea name="explain" rows={ 3 } />
              </FormItem>
            </ModalBody>
            <ModalFooter>
              <Button onClick={this.codeAddClose.bind(this)}>关闭</Button>
              <Button type="primary" htmlType="submit">确定</Button>
            </ModalFooter>
          </Modal>
        </Form>

      </Page>
    );
  }

}
CodeList.UIPage = page;
export default CodeList;