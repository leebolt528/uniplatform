

import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Tabs,Tab,Pagination,popup,Snackbar } from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '组件管理',
    css: [
        '../../css/singleBanner.min.css',
        '../../css/siteManager.min.css'
    ]
};

class componentManage extends Component {
    constructor(props){
        super(props);

        this.state = {
            index:1,
            size:10,
            total:0,
            componentList : [],
            crawlerType : []
        }
        this.getFetchComponentList = this.getFetchComponentList.bind(this);
        this.afterSubmit = this.afterSubmit.bind( this );
        this.beforeSubmit = this.beforeSubmit.bind(this);
        this.beforeAddSubmit = this.beforeAddSubmit.bind(this);
        this.afterAddSubmit = this.afterAddSubmit.bind(this);
        this.afterEditSubmit = this.afterEditSubmit.bind(this);
        this.getFetchEnum = this.getFetchEnum.bind(this);
    }

    componentDidMount() {
        this.getFetchComponentList();
        this.getFetchMachine();
        this.getServer();
        this.getFetchEnum();
    }

    getFetchComponentList(){
        let ID = this.props.page.id ? this.props.page.id : '';
        let param = new FormData();
        let { size,index } = this.state;
        param.append( 'size', size );
        param.append( 'number', index);

        let compName = this.state.comp_name ? this.state.comp_name : '';
        let compIp = this.state.comp_ip ? this.state.comp_ip : '';
        let compPort = this.state.comp_port ? this.state.comp_port : '';
        let compType = this.state.comp_type ? this.state.comp_type : 'ALL';
        param.append( 'name', compName );
        param.append( 'ip', compIp );
        param.append( 'port', compPort );
        param.append( 'type', compType );

        fetch( Uniplatform.context.url + '/dcci/crawler/search?serverId='+ ID , {
            credentials: 'same-origin',
            method: 'post',
            body:param
        } )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let component_List = data.data.content;
                let total = data.data.totalElements;
                this.setState({component_List,total});
                this.getList();
                if (data.data.content.length <= 0){
                    popup(<Snackbar message="暂无数据"/>);
                }
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    getList(){
        let {component_List} = this.state;
        let componentList = component_List.map(function (index) {
            let c =  {
                comId:index.id,
                name:index.name,
                maxSiteNum:index.maxSiteNum,
                path:index.path,
                port:index.port,
                server:index.server.ip,
                siteNum:index.siteNum,
                storagePath:index.storagePath,
                type:index.type
            }
            return c;
        });
        this.setState({componentList});
    }

    //机器管理跳转组件管理-根据机器id查询服务器
    getFetchMachine(){
        fetch( Uniplatform.context.url + '/dcci/server/list' , {
            credentials: 'same-origin',
            method: 'post'
        } )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let macheIp = '';
                let serId = this.props.page.id;
                data.data.content.map(function (item) {
                    if(item.id == serId){
                        macheIp = item.ip;
                    }
                });
                this.setState({macheIp});
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    //获取类型（枚举接口）
    getFetchEnum(){
        fetch( `${Uniplatform.context.url}/dcci/crawler/list/enum` , {
            credentials: 'same-origin',
            method: 'POST'
        } )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let crawlerType = data.data.crawlertype;
                this.setState({crawlerType});
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    getType(data){
        let result = '';
        let { crawlerType } = this.state;
        crawlerType.map((item,indexType) => {
            for ( var index in item ) {
                if(index == data){
                    result = item[data];
                }
            }
        });
        return result;
    }

    showNewModal(){
        let {macheIp,serverList} = this.state;
        let tbodyHtml = [];
        if(this.props.page.id){
            this.getServerId(macheIp);
            tbodyHtml.push(
                <FormItem disabled key="">
                    <Label>服务器:</Label>
                    <Input name="ip" value={macheIp} />
                </FormItem>
            );
        }else {
            tbodyHtml.push(
                <FormItem required={true} key="">
                    <Label>服务器:</Label>
                    <Select name="ip" dataSource={ serverList } placeholder="请选择服务器" onChange={this.getServerId.bind(this)} />
                </FormItem>
            );
        }
        this.setState({ tbodyHtml });
        this.setState( { visible: true } );
    }
    closeNewModal() {
        this.setState( { visible: false } );
    }
    showUpdateModal(id){
        this.getServer();
        this.getSingleComponent(id);
        this.setState( { updateVisible: true,comID:id } );
    }
    closeUpdateModal() {
        this.setState( { updateVisible: false } );
    }
    //获取服务器列表
    getServer(){
        fetch( Uniplatform.context.url + '/dcci/server/ip/list' , {
            credentials: 'same-origin',
            method: 'post'
        } )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let serverList = data.data.map(function (index) {
                    let serverIp = {
                        value :  index.ip,
                        text : index.ip
                    };
                    return serverIp;
                });
                this.setState({serverList:serverList,server:data.data});
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //服务器选择事件——获取相应id
    getServerId(sIP){
        let {server} = this.state;
        let sID;
        server.map(function (index) {
            if(sIP == index.ip){
                sID = index.id;
            }
        });
        this.setState({sID});
    }
    //修改组件——回显
    getSingleComponent(id){
        fetch( Uniplatform.context.url + '/dcci/crawler/get/'+ id , {
            credentials: 'same-origin',
            method: 'post'
        } )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let comName = data.data.name;
                let maxSiteNum = data.data.maxSiteNum;
                let path = data.data.path;
                let port = data.data.port;
                let serverId = data.data.serverId;
                let siteNum = data.data.siteNum;
                let storagePath = data.data.storagePath;
                let type = data.data.type;
                let serverIP = data.data.server.ip;
                this.setState({serverIP,comName,maxSiteNum,path,port,serverId,siteNum,storagePath,type});
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    exChangePagi(index, size) {
        this.setState({index, size}, () => this.getFetchComponentList());
    }

    beforeSubmit(data){
        let comp_name = data.name;
        let comp_ip = data.ip;
        let comp_port = data.port;
        let comp_type = data.type;
        this.setState({comp_name,comp_ip,comp_port,comp_type});
        let pageNumber = this.state.index;
        let pageSize = this.state.size;
        let size = "size";
        let number = "number";
        data[size] = pageSize;
        data[number] = pageNumber;
        return data;
    }

    //按条件搜索
    afterSubmit(data){
        if(data.code == 1){
            let component_List = data.data.content;
            let total = data.data.totalElements;
            this.setState({component_List,total});
            this.getList();
        }else{
            popup(<Snackbar message={data.message} />);
        }
    }

    //添加组件
    beforeAddSubmit(data){
        let port = data.port;
        let maxSiteNum = data.maxSiteNum;
        let siteNum = data.siteNum;
        if(/^\d*$/g.test(port)){
            if(/^\d*$/g.test(maxSiteNum)){
                if(/^\d*$/g.test(siteNum)){
                    return data;
                }else {
                    popup( <Snackbar message="采集点必须输入数字！" /> );
                    return false;
                }
            }else {
                popup( <Snackbar message="最大采集点必须输入数字！" /> );
                return false;
            }
        }else {
            popup( <Snackbar message="端口号必须输入数字！" /> );
            return false;
        }
    }
    afterAddSubmit(data){
        if(data.code == 1){
            this.getFetchComponentList();
            this.setState( { visible: false } );
            popup(<Snackbar message={data.message} />);
        }else{
            popup(<Snackbar message={data.message} />);
        }
    }

    //编辑组件
    afterEditSubmit(data){
        if(data.code == 1){
            this.getFetchComponentList();
            this.setState( { updateVisible: false } );
            popup(<Snackbar message={data.message} />);
        }else{
            popup(<Snackbar message={data.message} />);
        }
    }

    //删除组件
    handleDelete(id){
        console.log(id);
        const confirmShow = confirm("您确定要删除该组件吗?");
        if(!confirmShow){
            return false;
        }else {
            this.deleteComponent(id);
        }
    }
    deleteComponent(id){
        fetch( Uniplatform.context.url + '/dcci/crawler/delete/'+id, {
            credentials: 'same-origin',
            method: 'GET'} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                popup(<Snackbar message={data.message} />);
                this.getFetchComponentList();
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    render() {
        let {componentList,visible,updateVisible,index,size,total,serverList,serverIP,sID,
            comName,maxSiteNum,path,port,serverId,siteNum,storagePath,type,comID,tbodyHtml,crawlerType} = this.state;
        return (
            <Page>
                <SingleBanner prefix="数据中心" id={''}/>
                <Divider />
                <Row>
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                        <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                            <Com_Menu url={'/dcci/platform/component/list'}/>
                        </Col>
                        <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} style={{minHeight:'600px'}}>

                            <NavigationBar code={'componentManage'}/>

                            <Tabs>
                                <Tab title="采集器">
                                    <Col >
                                        <Form type="inline"
                                              method="get"
                                              action={ contextUrl + '/dcci/crawler/search' }
                                              async={true}
                                              onSubmit={ this.beforeSubmit }
                                              onAfterSubmit={this.afterSubmit}
                                        >
                                            <FormItem>
                                                <Label>名称:</Label>
                                                <Input  name="name" placeholder="请输入名称"/>
                                            </FormItem>
                                            <FormItem>
                                                <Label>服务器:</Label>
                                                <Input  name="ip" placeholder="请输入服务器"/>
                                            </FormItem>
                                            <FormItem>
                                                <Label>端口:</Label>
                                                <Input  name="port" placeholder="请输入端口"/>
                                            </FormItem>
                                            <FormItem>
                                                <Label>类型:</Label>
                                                <Select value={ 'ALL' } name="type">
                                                    <Option value='ALL'>全部</Option>
                                                    {
                                                        crawlerType.map((item,indexType) => {
                                                            for ( var index in item ) {
                                                                return (<Option key={index} value={index}>{item[index]}</Option>)
                                                            }
                                                        })
                                                    }
                                                </Select>
                                                {/*<Select name="type" placeholder="请选择类型">
                                                    <Option value="DOMESTIC_NEWS">国内新闻</Option>
                                                    <Option value="DOMESTIC_BLOG">国内博客</Option>
                                                    <Option value="DOMESTIC_FORUM">国内论坛</Option>
                                                    <Option value="DOMESTIC_ELECTRONIC">国内电子报</Option>
                                                    <Option value="DOMESTIC_PHONE">国内手机新闻</Option>
                                                    <Option value="BIDDING_NEWS">招投标新闻</Option>
                                                    <Option value="DOMESTIC_RETAILERS">国内电商</Option>
                                                    <Option value="HONGKONG_MACAO_TAIWAN_NEWS">港澳台新闻</Option>
                                                    <Option value="HONGKONG_MACAO_TAIWAN_FORUM">港澳台论坛</Option>
                                                    <Option value="DOMESTIC_VIDEO">国内视频</Option>
                                                    <Option value="DOMESTIC_WEIXIN">国内微信</Option>
                                                    <Option value="ABROAD_FORUM">国外论坛</Option>
                                                    <Option value="ABROAD_BLOG">国外博客</Option>
                                                    <Option value="ABROAD_NEWS">国外新闻</Option>
                                                </Select>*/}
                                            </FormItem>
                                            <Button htmlType="submit" type="default" style={{position:'relative',zIndex:1}}>查询</Button>
                                        </Form>
                                    </Col>
                                    <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                                        <Button type="default" size="medium" onClick={this.showNewModal.bind(this)}>添加</Button>
                                        <Divider />
                                    </Col>
                                    <Col>
                                        <Table dataSource={componentList} striped={ true } headBolder={ true } complex>
                                            <Column dataIndex="name" title="名称" scaleWidth = '10%' textAlign="center"/>
                                            <Column dataIndex="server" title="服务器" scaleWidth = '10%' textAlign="center"/>
                                            <Column dataIndex="port" title="端口" scaleWidth = '10%' textAlign="center"/>

                                            <Column title="类型" textAlign="center" scaleWidth="15%">
                                                { ( value ) => {
                                                    let result = this.getType(value.type);
                                                    return result;
                                                } }
                                            </Column>
                                            {/*<Column title="类型" scaleWidth = '15%' textAlign="center">
                                                { ( value ,type) => {
                                                    switch(value.type)
                                                    {
                                                        case 'DOMESTIC_NEWS':
                                                            return '国内新闻';
                                                        case 'DOMESTIC_BLOG':
                                                            return '国内博客';
                                                        case 'DOMESTIC_FORUM':
                                                            return '国内论坛';
                                                        case 'DOMESTIC_ELECTRONIC':
                                                            return '国内电子报';
                                                        case 'DOMESTIC_PHONE':
                                                            return '国内手机新闻';
                                                        case 'BIDDING_NEWS':
                                                            return '招投标新闻';
                                                        case 'DOMESTIC_RETAILERS':
                                                            return '国内电商';
                                                        case 'HONGKONG_MACAO_TAIWAN_NEWS':
                                                            return '港澳台新闻';
                                                        case 'HONGKONG_MACAO_TAIWAN_FORUM':
                                                            return '港澳台论坛';
                                                        case 'DOMESTIC_VIDEO':
                                                            return '国内视频';
                                                        case 'DOMESTIC_WEIXIN':
                                                            return '国内微信';
                                                        case 'ABROAD_FORUM':
                                                            return '国外论坛';
                                                        case 'ABROAD_BLOG':
                                                            return '国外博客';
                                                        case 'ABROAD_NEWS':
                                                            return '国外新闻';
                                                        default:
                                                            return '';
                                                    }
                                                } }
                                            </Column>*/}
                                            <Column dataIndex="maxSiteNum" title="最大采集点" scaleWidth = '10%' textAlign="center"/>
                                            <Column dataIndex="siteNum" title="采集点" scaleWidth = '10%' textAlign="center"/>
                                            <Column dataIndex="path" title="路径" scaleWidth = '10%' textAlign="center"/>
                                            <Column dataIndex="storagePath" title="存储路径" scaleWidth = '10%' textAlign="center"/>
                                            <Column title="操作" scaleWidth = '15%' textAlign="center" >
                                                { ( value ) => {
                                                    return (
                                                    <p className="complex-link">
                                                        <a onClick={this.showUpdateModal.bind(this,value.comId)}>修改</a>
                                                        <a onClick={this.handleDelete.bind(this,value.comId)}>删除</a>
                                                    </p>
                                                    )
                                                } }
                                            </Column>
                                        </Table>
                                        <Divider/>
                                        <Pagination align='right' index={index} total={total} size={size} onChange={this.exChangePagi.bind(this)}/>
                                        <Divider/>
                                    </Col>
                                </Tab>

                                <Tab title="同步器">选项卡二内容</Tab>
                                <Tab title="分析器">选项卡三内容</Tab>
                                <Tab title="es">选项卡四内容</Tab>
                                <Tab title="图片"></Tab>
                                <Tab title="分析器"></Tab>
                            </Tabs>
                        </Col>
                    </Col>
                </Row>


                <Form type="horizontal"
                      method="post"
                      action={ contextUrl + '/dcci/crawler/save' }
                      async={true}
                      onSubmit={ this.beforeAddSubmit }
                      onAfterSubmit={this.afterAddSubmit}
                >
                    <Modal visible={ visible } size="medium" onClose={ this.closeNewModal.bind( this ) }>
                        <ModalHeader>
                            添加组件
                        </ModalHeader>
                        <ModalBody>
                            <Row style={{margin:'2.5%'}}>
                                <Input name="serverId" type="hidden" value={sID}/>
                                <FormItem disabled>
                                    <Label>组件类型</Label>
                                    <Select name="componentType" value="CRAWLER">
                                        <Option value="CRAWLER">采集器</Option>
                                        <Option>同步器</Option>
                                        <Option>分析器</Option>
                                        <Option>es</Option>
                                    </Select>
                                </FormItem>

                                {tbodyHtml}
                                {/*<FormItem required={true}>
                                    <Label>服务器:</Label>
                                    <Select name="ip" dataSource={ serverList } placeholder="请选择服务器" onChange={this.getServerId.bind(this)} />
                                </FormItem>*/}
                                <FormItem required={true}>
                                    <Label>名称</Label>
                                    <Input name="name" placeholder="请输入名称"/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>端口</Label>
                                    <Input name="port" placeholder="请输入端口"/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>路径</Label>
                                    <Input name="path" placeholder="请输入路径"/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>存储路径 </Label>
                                    <Input name="storagePath" placeholder="请输入存储路径"/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>类型 </Label>
                                    <Select name="type" placeholder="请选择类型" value="DOMESTIC_NEWS">
                                        <Option value="DOMESTIC_NEWS">国内新闻</Option>
                                        <Option value="DOMESTIC_BLOG">国内博客</Option>
                                        <Option value="DOMESTIC_FORUM">国内论坛</Option>
                                        <Option value="DOMESTIC_ELECTRONIC">国内电子报</Option>
                                        <Option value="DOMESTIC_PHONE">国内手机新闻</Option>
                                        <Option value="BIDDING_NEWS">招投标新闻</Option>
                                        <Option value="DOMESTIC_RETAILERS">国内电商</Option>
                                        <Option value="HONGKONG_MACAO_TAIWAN_NEWS">港澳台新闻</Option>
                                        <Option value="HONGKONG_MACAO_TAIWAN_FORUM">港澳台论坛</Option>
                                        <Option value="DOMESTIC_VIDEO">国内视频</Option>
                                        <Option value="DOMESTIC_WEIXIN">国内微信</Option>
                                        <Option value="ABROAD_FORUM">国外论坛</Option>
                                        <Option value="ABROAD_BLOG">国外博客</Option>
                                        <Option value="ABROAD_NEWS">国外新闻</Option>
                                    </Select>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>最大采集点 </Label>
                                    <Input name="maxSiteNum" placeholder="请输入最大采集点"/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>采集点 </Label>
                                    <Input name="siteNum" placeholder="请输入采集点"/>
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
                      action={ contextUrl + '/dcci/crawler/update' }
                      async={true}
                      onSubmit={ this.beforeAddSubmit }
                      onAfterSubmit={this.afterEditSubmit}
                >
                    <Modal visible={ updateVisible } size="medium" onClose={ this.closeUpdateModal.bind( this ) }>
                        <ModalHeader>
                            修改组件信息
                        </ModalHeader>
                        <ModalBody>
                            <Row style={{margin:'2.5%'}}>
                                <Input name="id" type="hidden" value={comID}/>
                                <Input name="serverId" type="hidden" value={serverId}/>
                                <FormItem disabled>
                                    <Label>组件类型</Label>
                                    <Select name="componentType" value="CRAWLER">
                                        <Option value="CRAWLER">采集器</Option>
                                        <Option>同步器</Option>
                                        <Option>分析器</Option>
                                        <Option>es</Option>
                                    </Select>
                                </FormItem>
                                <FormItem disabled>
                                    <Label>服务器:</Label>
                                    <Select dataSource={ serverList } value={serverIP}/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>名称</Label>
                                    <Input name="name" placeholder="请输入名称" value={comName}/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>端口</Label>
                                    <Input name="port" placeholder="请输入端口" value={port}/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>路径</Label>
                                    <Input name="path" placeholder="请输入路径" value={path}/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>存储路径 </Label>
                                    <Input name="storagePath" placeholder="请输入存储路径" value={storagePath}/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>类型 </Label>
                                    <Select name="type" placeholder="请选择类型" value={type}>
                                        <Option value="DOMESTIC_NEWS">国内新闻</Option>
                                        <Option value="DOMESTIC_BLOG">国内博客</Option>
                                        <Option value="DOMESTIC_FORUM">国内论坛</Option>
                                        <Option value="DOMESTIC_ELECTRONIC">国内电子报</Option>
                                        <Option value="DOMESTIC_PHONE">国内手机新闻</Option>
                                        <Option value="BIDDING_NEWS">招投标新闻</Option>
                                        <Option value="DOMESTIC_RETAILERS">国内电商</Option>
                                        <Option value="HONGKONG_MACAO_TAIWAN_NEWS">港澳台新闻</Option>
                                        <Option value="HONGKONG_MACAO_TAIWAN_FORUM">港澳台论坛</Option>
                                        <Option value="DOMESTIC_VIDEO">国内视频</Option>
                                        <Option value="DOMESTIC_WEIXIN">国内微信</Option>
                                        <Option value="ABROAD_FORUM">国外论坛</Option>
                                        <Option value="ABROAD_BLOG">国外博客</Option>
                                        <Option value="ABROAD_NEWS">国外新闻</Option>
                                    </Select>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>最大采集点 </Label>
                                    <Input name="maxSiteNum" placeholder="请输入最大采集点" value={maxSiteNum}/>
                                </FormItem>
                                <FormItem required={true}>
                                    <Label>采集点 </Label>
                                    <Input name="siteNum" placeholder="请输入采集点" value={siteNum}/>
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
componentManage.UIPage = page;
export default componentManage;

