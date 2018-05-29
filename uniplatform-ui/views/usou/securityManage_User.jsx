import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Text,Pagination,context,Select,Option,Textarea,
    Modal,ModalHeader,ModalBody,ModalFooter,Form,FormItem,Label,Input,Snackbar,popup,Checkbox,Dialog } from 'epm-ui';
import { SingleBanner,util_bytesToSize,Footer,COMM_HeadBanner,UsouMenu,NavigationBar } from '../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: 'Usou',
    css: [
        'css/securityManage.min.css',
        'css/singleBanner.min.css',
        'css/leftnav.min.css'
    ]
};

class SecurityManage_User extends Component {
    constructor(props){
        super(props);
        this.state = {
            index: 1,
            size: 10,
            totalData:[],
            userList:[],
            pageStart:0,
            createUserVisible:false,
            updateUserVisible:false,
            userName:'',
            passwd:'',
            rolesData:'',
            currentUser:{}
        }
    }
    componentDidMount() {
        this.fetchUsouUserList();
        this.fetchUsouRoleList();
    }

    //获取用户列表
    fetchUsouUserList() {
        let { index,size } = this.state;
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/_user/list', {
            credentials: 'same-origin',
            method: 'GET'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let totalData=JSON.parse(data.data);
                this.setState({totalData},()=>this.exchangePage(index,size));
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //获取角色列表
    fetchUsouRoleList(){
        let { index,size } = this.state;
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/_role/list', {
            credentials: 'same-origin',
            method: 'GET'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let rolesData=JSON.parse(data.data).map((value,index)=>{
                     return {
                        text:value.name,
                        value:value.name
                    }
                });
                this.setState({rolesData},()=>this.exchangePage(index,size));
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //索引列表分页
    exchangePage(index,size){
        let userList = [];
        let totalData = this.state.totalData;
        let pageStart=index-1;
        this.setState({index,size});
        for(let i = (index-1) * size; i < index * size; i++){
            if(totalData[i]){
                //totalData[i].roles=totalData[i].roles.toString();
                userList.push(totalData[i])
            }
        }
        this.setState({userList,pageStart});
    }
    //编辑用户
    showUpdateModal(value){
        let currentUser=value;
         this.setState({updateUserVisible:true,currentUser});
    }
    closeUpdateUser(){
        this.resetUpdateUser();
        this.setState({updateUserVisible:false});
    }
    beforeUpdateUserSubmit(data1){
        let data={};
        data.roles=data1.roles.toString();
        data.passwd=data1.passwd;
        return data;
    }
    afterUpdateUserSubmit(data){
        data.status == '200' ? popup(<Snackbar message={JSON.parse(data.message).message}/>) + this.updateSuccess() : popup(<Snackbar
        message={JSON.parse(data.message).message}/>)

    }
    //删除用户
    deleteUserOne(value){
        let currentUser=value;
         this.setState({currentUser});
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleUserOneApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除此用户？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    handleUserOneApprove(after){
        let {currentUser}=this.state;
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/_user/'+currentUser.name+'/delete', {
            credentials: 'same-origin',
            method: 'GET'
            })
            .then((res) => res.json())
            .then((data) => {
                data.status == '200' ? popup(<Snackbar message={JSON.parse(data.message).message}/>)+this.updateSuccess(): popup(<Snackbar
                message={JSON.parse(data.message).message}/>)
            }).catch((err) => {
            console.log(err.toString());
            });
        after( true );
    }
    updateSuccess(){
        this.closeCreateUser();
        this.closeUpdateUser();
        this.setState({index:1,pageStart:0},()=>this.fetchUsouUserList());
    }
    //新建用户
    userNameValue(value){
        this.setState({userName:value});
    }
    showNewModal(){
        this.setState({createUserVisible:true});
    }
    closeCreateUser(){
        this.resetCreateUser();
        this.setState({createUserVisible:false});
    }
    beforeCreateUserSubmit(data1){
        let data={};
        data.passwd=data1.passwd;
        data.roles=data1.roles.toString();
        let {userName}=this.state;
        if(userName.length==0){
            popup(<Snackbar message="请输入用户名称！"/>) 
            return;
        }else if(data.passwd.length==0){
            popup(<Snackbar message="请设置密码！"/>) 
            return;
        }else if(data.roles.length==0){
             popup(<Snackbar message="请选择角色！"/>) 
             return;
        }
        return data;
    }
    afterCreateUserSubmit(data){
        data.status == '200' ? popup(<Snackbar message={JSON.parse(data.message).message}/>) + this.updateSuccess() : popup(<Snackbar
         message={JSON.parse(data.message).message}/>)
    }
    //清空回显
    formTirggerCreateUser(trigger){
        this.resetCreateUser = trigger.reset;
    }
    formTirggerUpdateUser(trigger){
        this.resetUpdateUser = trigger.reset;
    }
    render() {
        let {index,size,totalData,userList,pageStart,createUserVisible,updateUserVisible,userName,passwd,rolesData,currentUser} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id}/>*/}
                <Row style={{minHeight:'700px'}}>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <Divider/>
                        <UsouMenu url={'/usou/securityManage_User'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }}>
                       <Divider/>
                        <NavigationBar code={'securityManage_User'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name}/>
                        <Row>
                            <Button type="default" size="tiny" onClick={this.showNewModal.bind(this) } style={{float:'left',margin:'10px auto',position:'relative',zIndex:1}} >
                                <Icon icon="plus"/> 新建用户
                            </Button>
                        </Row>
                       
                        <Table dataSource={ userList } striped={ true } multiLine={ true } >
                            <Column title="序号" scaleWidth = '5%' textAlign="center">
                                  { ( value, index ) => { return Number(`${Number(this.state.pageStart)*Number(this.state.size)+Number(index)}`) + 1; } }
                            </Column>
                            <Column title="用户名" dataIndex="name" scaleWidth = '20%' textAlign="center"/>
                            <Column title="角色" scaleWidth = '55%'textAlign="center">
                                { ( value, index ) => { return value.roles.toString() }}
                            </Column>
                            <Column title="用户操作" scaleWidth = '20%' textAlign="center">
                                { ( value ) => {
                                    return (
                                        <div>
                                            <Button type="default" size="tiny" onClick={this.showUpdateModal.bind(this,value)}>
                                                <Icon icon="edit" /> 编辑
                                            </Button>
                                            <Button type="default" size="tiny" onClick={this.deleteUserOne.bind(this,value)}>
                                                <Icon icon="trash-o" /> 删除
                                            </Button>
                                        </div>
                                    )
                                } }
                            </Column>
                        </Table>
                        <Divider />
                        <Pagination index={ index }  total={ totalData.length>1000?1000:totalData.length } size={ size } align='right' onChange={this.exchangePage.bind(this)}/>
                        <Divider />
                    </Col>
                </Row>
                <Form method="post"
                          action={contextUrl + '/usou/cluster/'+this.props.page.id+'/_user/'+ userName +'/create'}
                          async={true}
                          type="horizontal"
                          enctype="multipart/form-data"
                          onSubmit={this.beforeCreateUserSubmit.bind(this)}
                          onAfterSubmit={this.afterCreateUserSubmit.bind(this)}
                          trigger={ this.formTirggerCreateUser.bind(this) }
                    >
                    <Modal visible={ createUserVisible } size="middle" onClose={ this.closeCreateUser.bind(this) }>
                        <ModalHeader>
                            创建用户
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>用户名称</Label>
                                <Input name="userName" placeholder="请输入用户名称" onChange={this.userNameValue.bind(this)}/>
                            </FormItem>
                            <FormItem>
                                <Label>密码</Label>
                                <Input name="passwd" placeholder="请设置密码"/>
                            </FormItem>
                            <FormItem>
                                <Label>角色</Label>
                                <Select name="roles" placeholder="请选择角色" dataSource={rolesData} multiple={ true }/>
                            </FormItem>
                            <Divider/>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeCreateUser.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form method="post"
                          action={contextUrl + '/usou/cluster/'+this.props.page.id+'/_user/'+currentUser.name+'/update'}
                          async={true}
                          type="horizontal"
                          enctype="multipart/form-data"
                          onSubmit={this.beforeUpdateUserSubmit.bind(this)}
                          onAfterSubmit={this.afterUpdateUserSubmit.bind(this)}
                          trigger={ this.formTirggerUpdateUser.bind(this) }
                    >
                    <Modal visible={ updateUserVisible } size="middle" onClose={ this.closeUpdateUser.bind(this) }>
                        <ModalHeader>
                            编辑用户
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>用户名称</Label>
                                <Input name="userName" value={currentUser.name} disabled/>
                            </FormItem>
                            <FormItem>
                                <Label>密码</Label>
                                <Input name="passwd" placeholder="请修改密码"/>
                            </FormItem>
                             <FormItem>
                                <Label>角色</Label>
                                <Select name="roles" value={currentUser.roles} placeholder="请选择角色" dataSource={rolesData} showClear={ false } multiple={ true }/>
                            </FormItem>
                            <Divider/>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeUpdateUser.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Footer/>
            </Page>
        );
    }
}
SecurityManage_User.UIPage = page;
export default SecurityManage_User;
