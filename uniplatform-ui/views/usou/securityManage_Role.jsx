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

class SecurityManage_Role extends Component {
    constructor(props){
        super(props);
        this.state = {
            index: 1,
            size: 10,
            totalData:[],
            roleList:[],
            pageStart:0,
            createRoleVisible:false,
            updateRoleVisible:false,
            roleName:'',
            clusterpriData:[],
            indexpriData:[],
            currentRole:{}
        }
    }
    componentDidMount() {
        this.fetchUsouRoleList();
        this.fetchUsouPriList();
    }

    //获取角色列表
    fetchUsouRoleList() {
        let { index,size } = this.state;
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/_role/list', {
            credentials: 'same-origin',
            method: 'GET'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let totalData=JSON.parse(data.data);
                this.setState({totalData},()=>this.exchangePage(index,size));
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //获取权限列表
    fetchUsouPriList(){
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/_privilege/list', {
            credentials: 'same-origin',
            method: 'GET'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let clusterpriData=JSON.parse(data.data).clusterpri.map((value)=>{
                    return {
                        text:value,
                        value:value
                    }
                });
                let indexpriData=JSON.parse(data.data).indexpri.map((value)=>{
                    return {
                        text:value,
                        value:value
                    }
                });
                this.setState({clusterpriData,indexpriData});
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //索引列表分页
    exchangePage(index,size){
        let roleList = [];
        let totalData = this.state.totalData;
        let pageStart=index-1;
        this.setState({index,size});
        for(let i = (index-1) * size; i < index * size; i++){
            if(totalData[i]){
                // totalData[i].clusterpri=totalData[i].clusterpri.toString();
                // totalData[i].indexpri=totalData[i].indexpri.toString();
                roleList.push(totalData[i]);
            }
        }
        console.log("roleList");
        console.log(roleList);
        this.setState({roleList,pageStart});
    }
    //编辑角色
    showUpdateModal(value){
        let currentRole=value;
        this.setState({updateRoleVisible:true,currentRole});
    }
    closeUpdateRole(){
        this.resetUpdateRole();
        this.setState({updateRoleVisible:false});
    }
    beforeUpdateRoleSubmit(data1){
        let data={};
        data.clusterpri=data1.clusterpri.toString();
        data.indexpri=data1.indexpri.toString();
        data.indexpattern=data1.indexpattern;
        return data;
    }
    afterUpdateRoleSubmit(data){
        data.status == '200' ? popup(<Snackbar message={JSON.parse(data.message).message}/>) + this.updateSuccess() : popup(<Snackbar
        message={JSON.parse(data.message).message}/>)

    }
    //删除角色
    deleteRoleOne(value){
        let currentRole=value;
         this.setState({currentRole});
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleRoleOneApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除此角色？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    handleRoleOneApprove(after){
        let {currentRole}=this.state;
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/_role/'+currentRole.name+'/delete', {
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
        this.closeCreateRole();
        this.closeUpdateRole();
        this.setState({index:1,pageStart:0},()=>this.fetchUsouRoleList());
    }
    //新建角色
    roleNameValue(value){
        this.setState({roleName:value});
    }
    showNewModal(){
        this.setState({createRoleVisible:true});
    }
    closeCreateRole(){
        this.resetCreateRole();
        this.setState({createRoleVisible:false});
    }
    beforeCreateRoleSubmit(data1){
        let data={};
        data.clusterpri=data1.clusterpri.toString();
        data.indexpri=data1.indexpri.toString();
        data.indexpattern=data1.indexpattern;

        let {roleName}=this.state;
        if(roleName.length==0){
            popup(<Snackbar message="请输入角色名称！"/>) 
            return;
        }else if(data.clusterpri.length==0){
            popup(<Snackbar message="请选择集群权限！"/>) 
            return;
        }else if(data.indexpri.length==0){
             popup(<Snackbar message="请选择索引权限！"/>) 
             return;
        }
        return data;
    }
    afterCreateRoleSubmit(data){
        data.status == '200' ? popup(<Snackbar message={JSON.parse(data.message).message}/>) + this.updateSuccess() : popup(<Snackbar
         message={JSON.parse(data.message).message}/>)
    }
    //清空回显
    formTirggerCreateRole(trigger){
        this.resetCreateRole = trigger.reset;
    }
    formTirggerUpdateRole(trigger){
        this.resetUpdateRole = trigger.reset;
    }
    render() {
        let {index,size,totalData,roleList,createRoleVisible,updateRoleVisible,roleName,clusterpriData,indexpriData,currentRole} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id}/>*/}
                <Row style={{minHeight:'700px'}}>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <Divider/>
                        <UsouMenu url={'/usou/securityManage_Role'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }}>
                       <Divider/>
                        <NavigationBar code={'securityManage_Role'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name}/>
                        <Row>
                            <Button type="default" size="tiny" onClick={this.showNewModal.bind(this) } style={{float:'left',margin:'10px auto',position:'relative',zIndex:1}} >
                                <Icon icon="plus"/> 新建角色
                            </Button>
                        </Row>
                       
                        <Table dataSource={ roleList } striped={ true } multiLine={ true } >
                            <Column title="序号" scaleWidth = '5%' textAlign="center">
                                  { ( value, index ) => { return Number(`${Number(this.state.pageStart)*Number(this.state.size)+Number(index)}`) + 1; } }
                            </Column>
                            <Column title="角色名" dataIndex="name" scaleWidth = '20%' textAlign="center"/>
                            <Column title="集群权限"   scaleWidth = '20%'textAlign="center">
                                { ( value, index ) => { return value.clusterpri.toString() }}
                            </Column>
                            <Column title="匹配表达式" dataIndex="indexpattern"  scaleWidth = '15%'textAlign="center"/>
                            <Column title="索引权限"  scaleWidth = '20%'textAlign="center">
                                { ( value, index ) => { return value.indexpri.toString()}}
                            </Column>
                            <Column title="角色操作" scaleWidth = '20%' textAlign="center">
                                { ( value ) => {
                                    return (
                                        <div>
                                            <Button type="default" size="tiny" onClick={this.showUpdateModal.bind(this,value)}>
                                                <Icon icon="edit" /> 编辑
                                            </Button>
                                            <Button type="default" size="tiny" onClick={this.deleteRoleOne.bind(this,value)}>
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
                          action={contextUrl + '/usou/cluster/'+this.props.page.id+'/_role/'+ roleName +'/create'}
                          async={true}
                          type="horizontal"
                          enctype="multipart/form-data"
                          onSubmit={this.beforeCreateRoleSubmit.bind(this)}
                          onAfterSubmit={this.afterCreateRoleSubmit.bind(this)}
                          trigger={ this.formTirggerCreateRole.bind(this) }
                    >
                    <Modal visible={ createRoleVisible } size="middle" onClose={ this.closeCreateRole.bind(this) }>
                        <ModalHeader>
                            创建角色
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>角色名称</Label>
                                <Input name="roleName" placeholder="请输入角色名称" onChange={this.roleNameValue.bind(this)}/>
                            </FormItem>
                            <FormItem>
                                <Label>集群权限</Label>
                                <Select name="clusterpri" placeholder="请选择集群权限" dataSource={clusterpriData} multiple={ true }/>
                            </FormItem>
                            <FormItem>
                                <Label>索引匹配表达式</Label>
                                <Input name="indexpattern" placeholder="请输入匹配表达式" />                             
                            </FormItem>
                             <FormItem>
                                <Label>索引权限</Label>
                                <Select name="indexpri" placeholder="请选择集群权限" dataSource={indexpriData} multiple={ true }/>
                            </FormItem>
                            <Divider/>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeCreateRole.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form method="post"
                          action={contextUrl + '/usou/cluster/'+this.props.page.id+'/_role/'+currentRole.name+'/update'}
                          async={true}
                          type="horizontal"
                          enctype="multipart/form-data"
                          onSubmit={this.beforeUpdateRoleSubmit.bind(this)}
                          onAfterSubmit={this.afterUpdateRoleSubmit.bind(this)}
                          trigger={ this.formTirggerUpdateRole.bind(this) }
                    >
                    <Modal visible={ updateRoleVisible } size="middle" onClose={ this.closeUpdateRole.bind(this) }>
                        <ModalHeader>
                            编辑角色
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>角色名称</Label>
                                <Input name="roleName" value={currentRole.name} disabled placeholder="请输入角色名称" onChange={this.roleNameValue.bind(this)}/>
                            </FormItem>
                            <FormItem>
                                <Label>集群权限</Label>
                                <Select name="clusterpri" value={currentRole.clusterpri} placeholder="请选择集群权限" dataSource={clusterpriData} showClear={ false } multiple={ true }/>
                            </FormItem>
                            <FormItem>
                                <Label>索引匹配表达式</Label>
                                <Input name="indexpattern" value={currentRole.indexpattern} placeholder="请输入匹配表达式" />                             
                            </FormItem>
                             <FormItem>
                                <Label>索引权限</Label>
                                <Select name="indexpri" value={currentRole.indexpri} placeholder="请选择集群权限" dataSource={indexpriData} showClear={ false } multiple={ true }/>
                            </FormItem>
                            <Divider/>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeUpdateRole.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Footer/>
            </Page>
        );
    }
}
SecurityManage_Role.UIPage = page;
export default SecurityManage_Role;
