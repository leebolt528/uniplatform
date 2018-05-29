import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
        Modal,ModalHeader,ModalBody,ModalFooter,popup,Snackbar,Pagination} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '机器管理',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

class machineManage extends Component {
  constructor(props){
    super(props);

    this.state = {
      index:1,
      size:10,
      total:0,
      machineList : []
    }
    this.getFetchMachineList = this.getFetchMachineList.bind(this);
    this.beforeQuerySubmit = this.beforeQuerySubmit.bind(this);
      this.afterSubmit = this.afterSubmit.bind( this );
      this.addAfterSubmit = this.addAfterSubmit.bind(this);
      this.updateAfterSubmit = this.updateAfterSubmit.bind(this);
  }

  componentDidMount() {
    this.getFetchMachineList();
  }

    getFetchMachineList(){
        let param = new FormData();
        let { size,index } = this.state;
        param.append( 'size', size );
        param.append( 'number', index);
        let machType = this.state.mach_type ? this.state.mach_type : 3;
        let machgroup = this.state.mach_group ? this.state.mach_group : 3;
        let machIP = this.state.mach_IP ? this.state.mach_IP : '';
    fetch( Uniplatform.context.url + '/dcci/server/list?group='+machgroup+'&machine='+machType+'&ip='+machIP , {
    credentials: 'same-origin',
    method: 'post',
        body : param
    } )
      .then( ( res ) => res.json() )
      .then( ( data ) => {
        let machineList = data.data.content;
        let total = data.data.totalElements;
        this.setState({machineList,total});
        if (data.data.content.length <= 0){
           popup(<Snackbar message="暂无数据"/>);
        }
      }).catch( ( err ) => console.log( err.toString() ) );
  }

    beforeQuerySubmit(data){
        let mach_type = data.machine;
        let mach_group = data.group;
        let mach_IP = data.ip;
        this.setState({mach_type,mach_group,mach_IP});
        let pageNumber = this.state.index;
        let pageSize = this.state.size;
        let size = "size";
        let number = "number";
        data[size] = pageSize;
        data[number] = pageNumber;
        return data;
    }
    afterSubmit(data){
        if(data.code == 1){
            let machineList = data.data.content;
            let total = data.data.totalElements;
            this.setState({machineList,total});
        }else{
            popup(<Snackbar message={data.message} />);
        }
    }

    //添加机器
    beforeSubmit(data){
      let cpu = data.cpu;
      let mem = data.mem;
      let disk = data.disk;
      if(/^\d*$/g.test(cpu)){
          if(/^\d*$/g.test(mem)){
              if(/^\d*$/g.test(disk)){
                  return data;
              }else {
                  popup( <Snackbar message="硬盘必须输入数字！" /> );
                  return false;
              }
          }else {
              popup( <Snackbar message="内存必须输入数字！" /> );
              return false;
          }
      }else {
          popup( <Snackbar message="CPU必须输入数字！" /> );
          return false;
      }
    }
    addAfterSubmit(data){
        popup(<Snackbar message={data.message} />);
        if(data.code == 1){
            this.setState( { visible: false } );
            this.getFetchMachineList();
        }
    }

    //修改机器
    getSingleMachine(id){
        let { machineList } = this.state;
        let cpu,disk,ip,license,machineCode,machineType,mem,name,pwd,systemType,user;
        machineList.map(function (machine) {
          if(machine.id == id){
            cpu = machine.cpu;
            disk = machine.disk;
            ip = machine.ip;
            license = machine.license;
            machineCode = machine.machineCode;
            machineType = machine.machineType;
            mem = machine.mem;
            name = machine.name;
            pwd  = machine.pwd;
            systemType = machine.systemType;
            user = machine.user;
            id = machine.id;
          }
        });
        this.setState({id,cpu,disk,ip,license,machineCode,machineType,mem,name,pwd,systemType,user});
    }
    updateAfterSubmit(data){
        popup(<Snackbar message={data.message} />);
        this.setState( { updateVisible: false } );
        this.getFetchMachineList();
    }

    showNewModal(){
        this.setState( { visible: true } );
    }
    closeNewModal() {
        this.setState( { visible: false } );
    }
    showUpdateModal(id){
        this.getSingleMachine(id);
        this.setState( { updateVisible: true } );
    }
    closeUpdateModal() {
        this.setState( { updateVisible: false } );
    }

    delete(id){
        const confirmShow = confirm("删除该机器会将其中的所有用户及其组件全部删除，是否全部删除?");
        if(!confirmShow){
            return false;
        }else {
            this.deleteMachine(id);
        }
    }
    //删除机器
    deleteMachine(id){
        fetch( Uniplatform.context.url + '/dcci/server/delete/'+id, {
            credentials: 'same-origin',
            method: 'GET'} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                popup(<Snackbar message={data.message} />);
                this.getFetchMachineList();
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    userManage(id){
        parent.location.href='/dcci/platform/machine/users?id='+id;
    }

    exChangePagi(index, size) {
        this.setState({index, size}, () => this.getFetchMachineList());
    }

    getcomponent(id){
        parent.location.href='/dcci/platform/component/list?id='+id;
    }

  render() {
    let {machineList,visible,updateVisible,index,size,total,group,machine,id,
        cpu,disk,ip,license,machineCode,machineType,mem,name,pwd,systemType,user} = this.state;
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

                    <NavigationBar code={'machineManage'}/>
                  
                    <Form type="inline"
                          method="get"
                          action={ contextUrl + '/dcci/server/list' }
                          async={true}
                          onSubmit={ this.beforeQuerySubmit }
                          onAfterSubmit={this.afterSubmit}
                    >
                        <FormItem>
                            <Label>地址:</Label>
                            <Input  name="ip" placeholder="请输入地址"/>
                        </FormItem>
                        <FormItem>
                            <Label>操作系统:</Label>
                            <Select value="3" name="group">
                                <Option value="3">全部</Option>
                                <Option value="1">windows</Option>
                                <Option value="2">linux</Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Label>类型:</Label>
                            <Select value="3" name="machine">
                                <Option value="3">全部</Option>
                                <Option value="1">物理机</Option>
                                <Option value="2">虚拟机</Option>
                            </Select>
                        </FormItem>
                        <Button htmlType="submit" type="default" style={{position:'relative',zIndex:1}}>查询</Button>
                    </Form>
                  
                    <Button style={{position:'relative',zIndex:1}} type="default" size="small" onClick={this.showNewModal.bind(this)}>添加</Button>
                    <Divider />
                  
                    <Table dataSource={machineList} striped={ true } headBolder={ true } complex>
                      <Column dataIndex="name" title="名称" scaleWidth = '10%' textAlign="center"/>
                      <Column dataIndex="ip" title="地址" scaleWidth = '10%' textAlign="center"/>
                      <Column title="类型" scaleWidth = '7%' textAlign="center">
                          { ( value ,machineType) => {
                              switch(value.machineType)
                              {
                                  case 'PHYSICAL':
                                      return '物理机';
                                  case 'VIRTUAL':
                                      return '虚拟机';
                                  default:
                                      return '';
                              }
                          } }
                      </Column>
                      <Column dataIndex="systemType" title="操作系统" scaleWidth = '10%' textAlign="center"/>
                      <Column dataIndex="cpu" title="cpu(核)" scaleWidth = '9%' textAlign="center"/>
                      <Column dataIndex="mem" title="内存(G)" scaleWidth = '7%' textAlign="center"/>
                      <Column dataIndex="disk" title="硬盘(G)" scaleWidth = '7%' textAlign="center"/>
                      <Column dataIndex="user" title="用户名" scaleWidth = '10%' textAlign="center"/>
                      <Column dataIndex="pwd" title="密码" scaleWidth = '10%' textAlign="center"/>
                      <Column title="操作" scaleWidth = '20%' textAlign="center" >
                          { ( value ) => {
                              return (
                              <p className="complex-link">
                                  <a onClick={this.showUpdateModal.bind(this,value.id)}>修改</a>
                                  <a onClick={this.delete.bind(this,value.id)}>删除</a>
                                  <a onClick={this.getcomponent.bind(this,value.id)}>查看组件</a>
                                  <a onClick={this.userManage.bind(this,value.id)}>用户管理</a>
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
                action={ `${contextUrl}/dcci/server/save`}
                async={true}
                onSubmit={ this.beforeSubmit }
                onAfterSubmit={this.addAfterSubmit}
          >
            <Modal visible={ visible } size="large" onClose={ this.closeNewModal.bind( this ) }>
              <ModalHeader>
                添加机器
              </ModalHeader>
              <ModalBody>
                <Row style={{margin:'2.5%'}}>
                    <Col size={{ normal: 24, small: 24, medium: 12, large: 12 }}>
                        <FormItem required={true}>
                            <Label>名称</Label>
                            <Input name="name" placeholder="请输入名称"/>
                        </FormItem>
                        <FormItem required={true}>
                            <Label>地址</Label>
                            <Input name="ip" type="url" placeholder="例如:127.0.0.1"/>
                        </FormItem>
                        <FormItem required={true}>
                            <Label>类型</Label>
                            <Select value="PHYSICAL" name="machineType">
                                <Option value="PHYSICAL">物理机</Option>
                                <Option value="VIRTUAL">虚拟机</Option>
                            </Select>
                        </FormItem>
                        <FormItem required={true}>
                            <Label>操作系统:</Label>
                            <Select value="WINDOWS" name="systemType">
                                <Option value="WINDOWS">windows</Option>
                                <Option value="LINUX">linux</Option>
                            </Select>
                        </FormItem>
                        <FormItem required={true}>
                            <Label>机器码</Label>
                            <Input name="machineCode" placeholder="请输入机器码"/>
                        </FormItem>
                        <FormItem required={true}>
                            <Label>license </Label>
                            <Input name="license" placeholder="请输入license"/>
                        </FormItem>
                    </Col>
                    <Col size={{ normal: 24, small: 24, medium: 12, large: 12 }}>
                        <FormItem required={true}>
                            <Label>CPU </Label>
                            <Input name="cpu" placeholder="请输入CPU"/>
                        </FormItem>
                        <FormItem required={true}>
                            <Label>内存 </Label>
                            <Input name="mem" placeholder="请输入内存大小"/>
                        </FormItem>
                        <FormItem required={true}>
                            <Label>硬盘 </Label>
                            <Input name="disk" placeholder="请输入硬盘大小"/>
                        </FormItem>
                        <FormItem>
                            <Label>用户名</Label>
                            <Input name="user" placeholder="请输入用户名"/>
                        </FormItem>
                        <FormItem>
                            <Label>密码</Label>
                            <Input name="pwd" placeholder="请输入密码"/>
                        </FormItem>
                    </Col>
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
                action={ contextUrl + '/dcci/server/update' }
                async={true}
                onAfterSubmit={this.updateAfterSubmit}
          >
            <Modal visible={ updateVisible } size="medium" onClose={ this.closeUpdateModal.bind( this ) }>
              <ModalHeader>
                修改机器信息
              </ModalHeader>
              <ModalBody>
                <Row style={{margin:'2.5%'}}>
                  <Input name="id" value={id} type="hidden"/>
                  <FormItem required={true}>
                    <Label>名称</Label>
                    <Input name="name" placeholder="请输入名称" value={name}/>
                  </FormItem>
                  <FormItem disabled>
                    <Label>地址</Label>
                    <Input name="ip" placeholder="请输入地址" value={ip}/>
                  </FormItem>
                  <FormItem disabled>
                    <Label>类型</Label>
                    <Select value={ machineType } name="machineType">
                      <Option value="PHYSICAL">物理机</Option>
                      <Option value="VIRTUAL">虚拟机</Option>
                    </Select>
                  </FormItem>
                  <FormItem disabled>
                    <Label>操作系统:</Label>
                    <Select value={ systemType } name="systemType">
                      <Option value="WINDOWS">windows</Option>
                      <Option value="LINUX">linux</Option>
                    </Select>
                  </FormItem>
                  <FormItem disabled>
                    <Label>机器码</Label>
                    <Input name="machineCode" placeholder="请输入机器码" value={machineCode}/>
                  </FormItem>
                  <FormItem required={true}>
                    <Label>license </Label>
                    <Input name="license" placeholder="请输入license" value={license}/>
                  </FormItem>
                  <FormItem disabled>
                    <Label>CPU </Label>
                    <Input name="cpu" placeholder="请输入机器码" value={cpu}/>
                  </FormItem>
                  <FormItem disabled>
                    <Label>内存 </Label>
                    <Input name="mem" placeholder="请输入机器码" value={mem}/>
                  </FormItem>
                  <FormItem disabled>
                    <Label>硬盘 </Label>
                    <Input name="disk" placeholder="请输入机器码" value={disk}/>
                  </FormItem>
                  <FormItem>
                    <Label>用户名</Label>
                    <Input name="user" placeholder="请输入机器码" value={user}/>
                  </FormItem>
                  <FormItem>
                    <Label>密码</Label>
                    <Input name="pwd" placeholder="请输入机器码" value={pwd}/>
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
machineManage.UIPage = page;
export default machineManage;