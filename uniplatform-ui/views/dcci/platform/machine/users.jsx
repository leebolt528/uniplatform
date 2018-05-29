import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
        Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: 'mac_pwd',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

class mac_pwdManage extends Component {
  constructor(props){
    super(props);

    this.state = {
        index:1,
        size:10,
        total:0,
        userList : [],
        visible:false,
        updateVisible:false
    }
    this.getFetchPasswordList = this.getFetchPasswordList.bind(this);
      this.afterSubmit = this.afterSubmit.bind( this );
      this.updateAfterSubmit = this.updateAfterSubmit.bind(this);
  }

  componentDidMount() {
    this.getFetchPasswordList();
  }

  getFetchPasswordList(){
      let param = new FormData();
      let { size,index } = this.state;
      param.append( 'pageSize', size );
      param.append( 'pageNum', index);
    fetch( Uniplatform.context.url + '/dcci/server/pwd/list?serverId='+this.props.page.id, {
    credentials: 'same-origin',
    method: 'post',
        body:param
    } )
      .then( ( res ) => res.json() )
      .then( ( data ) => {
              let userList = data.data.content;
              let total = data.data.totalElements;
              this.setState({userList,total});
          if (data.data.content.length <= 0){
              popup(<Snackbar message="暂无数据"/>);
          }
      }).catch( ( err ) => console.log( err.toString() ) );
  }

    showNewModal(){
        this.setState( { visible: true } );
    }
    closeNewModal() {
        this.setState( { visible: false } );
    }
    showUpdateModal(id){
        this.getSingleUser(id);
        this.setState( { updateVisible: true } );
    }
    closeUpdateModal() {
        this.setState( { updateVisible: false } );
    }

    afterSubmit(data){
        popup(<Snackbar message={data.message} />);
        this.setState( { visible: false } );
        this.getFetchPasswordList();
    }

    //修改用户信息
    getSingleUser(id){
        let { userList } = this.state;
        let UserId,operator,purpose,pwd,serverId,user;
        userList.map(function (userIndex) {
            if(userIndex.id == id){
                UserId = userIndex.id;
                operator = userIndex.operator;
                purpose = userIndex.purpose;
                pwd = userIndex.pwd;
                serverId = userIndex.serverId;
                user = userIndex.user;
            }
        });
        this.setState({UserId,operator,purpose,pwd,serverId,user});
    }
    updateAfterSubmit(data){
        popup(<Snackbar message={data.message} />);
        this.setState( { updateVisible: false } );
        this.getFetchPasswordList();
    }

    //删除用户
    delete(value){
        const confirmShow = confirm("确定要删除该用户?");
        if (!confirmShow) {
            return false;
        }
        fetch( `${Uniplatform.context.url}/dcci/server/pwd/delete/${value.id}` , {
            credentials: 'same-origin',
            method: 'POST'
        } )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                if(data.code == '1'){
                    popup(<Snackbar message={data.message}/>);
                    this.getFetchPasswordList();
                }else{
                    popup(<Snackbar message={data.message}/>);
                }
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    exChangePagi(index, size) {
        this.setState({index, size}, () => this.getFetchPasswordList());
    }


  render() {
    let {userList,visible,updateVisible,UserId,operator,purpose,pwd,serverId,user,index,size,total} = this.state;
    return (
        <Page>
            <SingleBanner prefix="数据中心" id={''}/>
            <Divider />
            <Row>
               <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                  <Com_Menu url={'/dcci/platform/machine/list'}/>
                </Col>
                <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} style={{minHeight:'600px'}}>

                  <NavigationBar code={'mac_pwdManage'} innerId={this.props.page.id}/>

                  <Button type="default" size="small" onClick={this.showNewModal.bind(this)}>添加</Button>
                  <Divider />
                  <Table dataSource={userList} striped={ true } headBolder={ true } complex>
                    <Column dataIndex="user" title="用户名" textAlign="center"/>
                    <Column dataIndex="pwd" title="密码" textAlign="center"/>
                    <Column dataIndex="operator" title="使用者" textAlign="center"/>
                    <Column dataIndex="purpose" title="用途" textAlign="center"/>
                    <Column title="操作" textAlign="center">
                        { ( value ) => {
                            return (
                            <p className="complex-link">
                                <a onClick={this.showUpdateModal.bind(this,value.id)}>修改</a>
                                <a onClick={this.delete.bind(this,value)}>删除</a>
                            </p>
                            )
                        } }
                    </Column>
                  </Table>
                    <Divider/>
                    <Pagination align='right' index={index} total={total} size={size} onChange={this.exChangePagi.bind(this)}/>
                    <Divider/>
                </Col>
              </Col>
            </Row>
          <Form type="horizontal"
                method="post"
                action={ contextUrl + '/dcci/server/pwd/save' }
                async={true}
                onAfterSubmit={this.afterSubmit}
          >
            <Modal visible={ visible } size="medium" onClose={ this.closeNewModal.bind( this ) }>
              <ModalHeader>
                添加用户
              </ModalHeader>
              <ModalBody>
                <Row style={{margin:'2.5%'}}>
                    <Input name="serverId" value={this.props.page.id} type="hidden"/>
                  <FormItem required={true}>
                    <Label>用户名</Label>
                    <Input name="user" placeholder="请输入用户名"/>
                  </FormItem>
                  <FormItem required={true}>
                    <Label>密码</Label>
                    <Input name="pwd" placeholder="请输入密码"/>
                  </FormItem>
                  <FormItem>
                    <Label>使用者</Label>
                    <Input name="operator" placeholder="请输入机器码"/>
                  </FormItem>
                  <FormItem name="textarea">
                    <Label>用途</Label>
                    <Textarea name="purpose" rows={10} style={{resize:'none'}} placeholder="请输入备注"/>
                  </FormItem>
                </Row>
              </ModalBody>
              <ModalFooter>
                <Button onClick={ this.closeNewModal.bind( this ) }>关闭</Button>
                <Button type="primary" htmlType="submit">提交</Button>
              </ModalFooter>
            </Modal>
          </Form>

          <Form type="horizontal"
                method="post"
                action={ contextUrl + '/dcci/server/pwd/update' }
                async={true}
                onAfterSubmit={this.updateAfterSubmit}
          >
            <Modal visible={ updateVisible } size="medium" onClose={ this.closeUpdateModal.bind( this ) }>
              <ModalHeader>
                修改用户信息
              </ModalHeader>
              <ModalBody>
                <Row style={{margin:'2.5%'}}>
                  <Input name="id" value={UserId} type="hidden"/>
                  <FormItem required={true}>
                    <Label>用户名</Label>
                    <Input name="user" placeholder="请输入用户名" value={user}/>
                  </FormItem>
                  <FormItem required={true}>
                    <Label>密码</Label>
                    <Input name="pwd" placeholder="请输入密码" value={pwd}/>
                  </FormItem>
                  <FormItem>
                    <Label>使用者</Label>
                    <Input name="operator" placeholder="请输入使用者" value={operator}/>
                  </FormItem>
                  <FormItem name="textarea">
                    <Label>用途</Label>
                    <Textarea name="purpose" rows={10} style={{resize:'none'}} placeholder="请输入备注" value={purpose}/>
                  </FormItem>
                </Row>
              </ModalBody>
              <ModalFooter>
                <Button onClick={ this.closeUpdateModal.bind( this ) }>关闭</Button>
                <Button type="primary" htmlType="submit">提交</Button>
              </ModalFooter>
            </Modal>
          </Form>
            <Footer />
        </Page>
    );
  }

}
mac_pwdManage.UIPage = page;
export default mac_pwdManage;