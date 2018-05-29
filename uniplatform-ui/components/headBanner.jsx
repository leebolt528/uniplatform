import React, { Component } from 'react';
import {Text,Dropdown,Icon,Form,Modal,ModalHeader,ModalBody,ModalFooter,FormItem,Label,Input,Button,popup,
    Snackbar} from 'epm-ui';

const contextUrl = '/uniplatform';

class COMM_HeadBanner extends Component {
    constructor( props ) {
        super( props );
        this.state = { visible: false };
        this.handleIndexClick = this.handleIndexClick.bind(this);
        this.handleClick = this.handleClick.bind( this );
        this.handleDataClick = this.handleDataClick.bind(this);
        this.handleNLPClick = this.handleNLPClick.bind(this);
        this.handleNLP_PlatformClick = this.handleNLP_PlatformClick.bind(this);
        this.handleSDKClick = this.handleSDKClick.bind(this);
        this.handleExampleCodeClick = this.handleExampleCodeClick.bind(this);
        this.handleManualClick = this.handleManualClick.bind(this);
        this.handleSDKManualClick = this.handleSDKManualClick.bind(this);
        this.getLoginStatus = this.getLoginStatus.bind(this);
    }

    componentDidMount() {
        this.getLoginStatus();
    }

    getLoginStatus(){
        fetch(Uniplatform.context.url + '/nlap/admin/user/loginfo', {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code == 1){
                    let userName = data.data.userName;
                    this.setState({userName});
                }
            }).catch((err) => console.log(err.toString()));
    }

    handleIndexClick(){
        window.open('/common/index','_self');
    }

    handleClick() {
        window.open('/usou/index','_self');
    }

    handleDataClick(){
        window.open('/dcci/org/index','_self');
    }

    handleNLPClick(){
        window.open('/nlap/index','_self');
    }

    handleNLP_PlatformClick(){
        window.open('/nlap/platform/dictionary','_self');
    }

    handleSDKClick(){
        window.open('/uniplatform/nlap/admin/file/downloadTemplate?fileName=sdk.jar','_self');
    }
    handleExampleCodeClick(){
        window.open('/uniplatform/nlap/admin/file/downloadTemplate?fileName=example.zip','_self');
    }
    handleManualClick(){
        window.open('/uniplatform/nlap/admin/file/downloadTemplate?fileName=文本分析-sdk使用手册v1.4.doc','_self');
    }
    handleSDKManualClick(){
        window.open('/uniplatform/nlap/admin/file/downloadTemplate?fileName=sdk使用手册.pdf','_self');
    }

    //登录模态框
    loginModal(){
        this.setState( { visible: true } );
    }
    handleClose() {
        this.setState( { visible: false } );
    }

    beforeSubmit(data){
        if(!data.account.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请输入账号"/>);
            return false;
        }else if(!data.passwd.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请输入密码"/>);
            return false;
        }else {
            return true;
        }
    }
    afterSubmit(data){
        if(data.code != 1){
            popup(<Snackbar message={data.message}/>);
        }else {
            popup(<Snackbar message={data.message}/>);
            let userName = data.data.userName;
            this.setState({userName,visible: false});
        }
    }

    //退出登陆
    outLoginModal(){
        fetch(Uniplatform.context.url + '/nlap/admin/user/logout', {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                popup(<Snackbar message={data.message}/>);
                location.reload();
            }).catch((err) => console.log(err.toString()));
    }

    render() {
        const { visible,userName } = this.state;
        const { prefix } = this.props;
        const dataSource = [
            {
                name: '知识库'
            },
            {
                name: '文本',
                onClick:( events )=>{
                    this.handleNLPClick();
                }
            },
            {
                name: '全文检索',
                onClick: ( events )=>{  //可定义onClick事件
                    this.handleClick();
                }
            },
            {
                name: '数据中心',
                onClick: ( events )=>{  //可定义onClick事件
                    this.handleDataClick();
                }
            }
        ];
        const docSource = [
            {
                name: 'SDK',
                onClick: ( events )=>{
                    this.handleSDKClick();
                }
            },
            {
                name: '示例代码',
                onClick:( events )=>{
                    this.handleExampleCodeClick();
                }
            },
            {
                name: '使用手册',
                onClick: ( events )=>{
                    this.handleManualClick();
                }
            },
            {
                name: 'SDK使用手册',
                onClick: ( events )=>{
                    this.handleSDKManualClick();
                }
            }
        ];
        return (
            <div className="header-divider">
                <div className="header-dividerL">
                    <span className="header-bonc" > BONC </span>
                    <span className="header-color" > { prefix }</span>
                </div>
                <div className="header-dividerR">
                    <div className="header-dividerR-menu-link" onClick={this.handleIndexClick}>首页</div>
                    {/*<div className="header-dividerR-menu-link">
                     <Dropdown dataSource={ dataSource } >
                     <Dropdown.Trigger>
                     <span>产品 <Icon icon="sort-down"></Icon></span>
                     </Dropdown.Trigger>
                     <Dropdown.Content position={ 'bottom-left' } />
                     </Dropdown>
                     </div>*/}
                    <div className="header-dividerR-menu-link" onClick={this.handleNLPClick}>产品</div>
                    <div className="header-dividerR-menu-link">解决方案</div>
                    <div className="header-dividerR-menu-link">
                        <Dropdown dataSource={ docSource } >
                            <Dropdown.Trigger>
                                <span>文档 <Icon icon="sort-down"></Icon></span>
                            </Dropdown.Trigger>
                            <Dropdown.Content position={ 'bottom-left' } />
                        </Dropdown>
                    </div>
                    <div className="header-dividerR-menu-link" onClick={this.handleNLP_PlatformClick}>管控平台</div>
                    {userName ?
                        <div className="header-dividerR-menu-link">{userName}</div>
                        :
                        <div className="header-dividerR-menu-link" onClick={this.loginModal.bind(this)}>
                            登录/注册
                        </div>
                    }
                    <div className="header-dividerR-menu-link" onClick={this.outLoginModal.bind(this)}>退出</div>
                </div>
                <Form type="horizontal"
                      method="post"
                      action={contextUrl + '/nlap/admin/user/login'}
                      async={true}
                      onSubmit={ this.beforeSubmit.bind(this) }
                      onAfterSubmit={this.afterSubmit.bind(this)}
                >
                    <Modal visible={ visible } size="medium" onClose={ this.handleClose.bind( this ) }>
                        <ModalHeader>
                            用户登录
                        </ModalHeader>
                        <ModalBody>
                            <div style={{margin:'0 5%',textAlign:'center'}}>
                                <FormItem>
                                    <Label style={{width:'10%'}}>账号</Label>
                                    <Input name="account" placeholder="请输入账号"/>
                                </FormItem>
                                <FormItem>
                                    <Label style={{width:'10%'}}>密码</Label>
                                    <Input name="passwd" type="password" placeholder="请输入密码" />
                                </FormItem>
                            </div>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.handleClose.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">登录</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
            </div>
        );
    }

}

export default COMM_HeadBanner;