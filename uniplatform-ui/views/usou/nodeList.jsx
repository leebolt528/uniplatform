import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Text,Pagination,context,
   Snackbar,popup,Icon,ButtonGroup,FormItem,Input,Form,CheckboxGroup,Checkbox } from 'epm-ui';
import { SingleBanner,util_formatTime,util_bytesToSize,Footer,COMM_HeadBanner,UsouMenu,NavigationBar } from '../../components/uniplatform-ui';

const page = {
    title: 'Usou',
    css: [
        'css/clusters.min.css',
        'css/singleBanner.min.css',
        'css/leftnav.min.css'
    ]
};

class NodeList extends Component {
    constructor(props){
        super(props);
        this.state = {
            index:1,
            size:5,
            total:0,
            state:[],
            nodes:[],
            nodesStats:[],
            nodeList:[],
            totalData:[],
            keywords:'',
            checkValue:[]
        }
        this.beforeSubmit = this.beforeSubmit.bind( this );
        this.fetchNodeList = this.fetchNodeList.bind(this);
        this.exchangePage =  this.exchangePage.bind(this);
        this.handleValue = this.handleValue.bind(this);
        this.getCheckValue = this.getCheckValue.bind(this);
    }
    componentDidMount() {
        this.fetchNodeList();
    }

    handleValue(data){
        let checkValue=data;
        let index=1;
        this.setState({checkValue,index});
        this.fetchNodeList(); 
    }

    beforeSubmit( data ) {
        let keywords = data.name;
        let index = 1;
        this.setState( { index,keywords }, () => this.fetchNodeList() );
        return false;
    }

    fetchNodeList() {
        let { keywords,index,size } = this.state;
        index=1;
        keywords = (keywords != "") ? "/" + keywords : keywords;
        fetch( Uniplatform.context.url + '/usou/cluster/' + this.props.page.id + '/_node' + keywords, {
            credentials: 'same-origin',
            method : 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let { nodes, nodesStats, state } = data;
                this.setState( { nodes, nodesStats, state } );
                let totalData = this.getCountNodes();
                this.setState( { totalData } );
                this.getCheckValue();
                this.exchangePage(index,size);
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    getCheckValue(){
        let totalData=this.state.totalData;
        let checkValue=this.state.checkValue;
        let newTotalData=[];
        totalData.map(function(data){
            if(data.nodeInfo.roles.length===0){
                data.nodeInfo.roles.push("client");
            };
            let count=0;
            checkValue.forEach(function(element){
                if(data.nodeInfo.roles.indexOf(element)==-1){
                    count++;
                }
            })
            if(count===0){
                newTotalData.push(data);
            }
        });
        this.setState({totalData:newTotalData});
    }

    getCountNodes(){
        let {nodes,nodesStats,state} = this.state;
        if(nodes.hasOwnProperty( "nodes")){
            let cluster_nodes = Object.keys(nodes.nodes).map(function(nodeId) {
                let nodeAssign = {
                    nodeId : nodeId,
                    nodeStats : nodesStats.nodes[nodeId],
                    nodeInfo : nodes.nodes[nodeId],
                    nodeMaster : nodeId === state.master_node ? "realstar" : "emptystar"
                };
                return nodeAssign;
            });
            return cluster_nodes;
        }
    }
    getNodeDeail(nodes,nodeMaster){
        let real_icon,
            emp_icon,
            search_icon,
            deit_icon;
        if(nodes.hasOwnProperty("roles")){
            let roles = Object.keys(nodes.roles);
            if(roles.length > 0){
                roles.map(function (dataRole){
                    if(nodes.roles[dataRole] == "data"){
                        deit_icon = <Icon icon="file"/>;
                    }else if(nodes.roles[dataRole] == "client"){
                        search_icon = <Icon icon="search"/>;
                    }else if(nodes.roles[dataRole] == "master"){
                        if(nodeMaster == "realstar"){
                            real_icon = <Icon icon="star"/>;
                        }else{
                            emp_icon = <Icon icon="star-o"/>;
                        }
                    }
                });
            }else{
                emp_icon = <Icon icon="star-o"/>;
                search_icon = <Icon icon="search"/>;
            }
        }else{
            emp_icon = <Icon icon="star-o"/>;
            search_icon = <Icon icon="search"/>;
        }
        let detail = <div>
            <div style={{float:'left',margin:'auto 15px'}}>
                <p>{real_icon}</p>
                <p>{emp_icon}</p>
                <p>{search_icon}</p>
                <p>{deit_icon}</p>
            </div>
            <div style={{float:'left',textAlign:'left'}}>
                <p>{nodes.name}</p>
                <p>{nodes.ip}</p>
                <p>{nodes.http_address}</p>
                <p>JVM:{nodes.jvm.version} ES:{nodes.version}</p>
            </div>
        </div>;
        return detail;
    }
    getNodeLode(nodeStats){
        return <p>1 min avg : {nodeStats.os.load_average > 0 ? nodeStats.os.load_average : 0}</p>;
    }
    getNodeCpu(nodeStats){
        return <p>{nodeStats.os.cpu_percent > 0 ? nodeStats.os.cpu_percent : 0} %</p>;
    }
    getNodeMem(nodeStats){
        let mem = <div>
            <p>{nodeStats.jvm.mem.heap_used_percent} %</p>
            <p>{util_bytesToSize(nodeStats.jvm.mem.heap_used_in_bytes)}</p>
            <p>{util_bytesToSize(nodeStats.jvm.mem.heap_max_in_bytes)}</p>
        </div>;
        return mem;
    }
    getNodeFsTotal(nodeStats){
        let usebyte = (nodeStats.fs.total.total_in_bytes) - (nodeStats.fs.total.free_in_bytes);
        let usePercent = (usebyte / (nodeStats.fs.total.total_in_bytes) ).toFixed(2);
        let fsTotal = <div>
            <p>{usePercent} %</p>
            <p>{util_bytesToSize(usebyte)}</p>
            <p>{util_bytesToSize(nodeStats.fs.total.total_in_bytes)}</p>
        </div>;
        return fsTotal;
    }
    getNodeTime(nodeStats){
        return <p>{util_formatTime(nodeStats.jvm.uptime_in_millis)}</p>;
    }
    exchangePage(index,size){
        let nodeList = [];
        let totalData = this.state.totalData;
        let total = totalData.length;
        this.setState({index,size,total});
        for(let i = (index-1) * size; i < index * size; i++){
            if(totalData[i]){
                nodeList.push(totalData[i])
            }
        }
        this.setState({nodeList});
    }

    getOpenNodeDetail(nodeId,name){
        window.open('/usou/nodeDetail?id=' + this.props.page.id + "&nodeId=" + nodeId+"&name="+name);
    }

    handleKeyPress( event ) {
        let value={};
        value.name=this.getValueInput();
        if (event.which==13){
            this.beforeSubmit(value);
        }
    }
    wordSearchAuto(data){
        if(data == ''){
            this.setState({ keywords : '' }, () => this.fetchNodeList());
        }
    }
    searchAuto(){
        this.setState({ keywords : '' }, () => this.fetchNodeList());
    }

    formGetterInput(getter){
        this.getValueInput=getter.value;
    }

    render() {
        let {nodeList,index,size,total} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id} />*/}
                 <Divider/>
                <div style={{minHeight: '680px'}}>
                    <Row>
                        <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                            <UsouMenu url={'/usou/nodeList'} id={this.props.page.id} name={this.props.page.name}/>
                        </Col>
                        <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }}>
                            <Row>
                                <NavigationBar code={'nodeManage'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name}/>
                            </Row>
                            <Row>
                                <Form
                                    type="inline"
                                    action="fack"
                                    style={{float:'right'}}
                                    onSubmit={ this.beforeSubmit }>
                                    <FormItem name="nodeList" type="inline">
                                        <Input  name="name" placeholder="请输入节点名称"
                                                onKeyPress={ this.handleKeyPress.bind(this)} type={'search'}
                                                getter={ this.formGetterInput.bind(this) }
                                                onChange={this.wordSearchAuto.bind(this)}
                                                onClear={this.searchAuto.bind(this)}/>
                                    </FormItem>
                                    <Button htmlType="submit" type="default" style={{marginRight:'20px'}}>查询</Button>
                                    <FormItem name="nodeList" type="inline">
                                        <CheckboxGroup name="rGroup" type="inline" onChange={this.handleValue}>
                                            <Checkbox value="master"><Icon icon="th-large"/>主节点</Checkbox>
                                            <Checkbox value="data"><Icon icon="th-list"/>数据节点</Checkbox>
                                            <Checkbox value="client"><Icon icon="th"/>客户端节点</Checkbox>
                                        </CheckboxGroup>
                                    </FormItem>
                                </Form>
                            </Row>
                            <Row>
                                <Table dataSource={ nodeList } striped={ true } multiLine={ true } >
                                    <Column title="节点" textAlign="center">
                                        { (value) => {
                                            let result = this.getNodeDeail(value.nodeInfo,value.nodeMaster);
                                            return (
                                                <span>
                                                {
                                                    <a onClick={this.getOpenNodeDetail.bind(this,value.nodeId,this.props.page.name)}>{result}</a>
                                                }
                                            </span>
                                            )
                                        }}
                                    </Column>
                                    <Column title="负载情况" textAlign="center">
                                        { (value) => {
                                            let result = this.getNodeLode(value.nodeStats);
                                            return result;
                                        }}
                                    </Column>
                                    <Column title="cpu使用率" textAlign="center">
                                        { (value) => {
                                            let result = this.getNodeCpu(value.nodeStats);
                                            return result;
                                        }}
                                    </Column>
                                    <Column title="内存使用率" textAlign="center">
                                        { (value) => {
                                            let result = this.getNodeMem(value.nodeStats);
                                            return result;
                                        }}
                                    </Column>
                                    <Column title="硬盘使用率" textAlign="center">
                                        { (value) => {
                                            let result = this.getNodeFsTotal(value.nodeStats);
                                            return result;
                                        }}
                                    </Column>
                                    <Column title="运行时间" textAlign="center">
                                        { (value) => {
                                            let result = this.getNodeTime(value.nodeStats);
                                            return result;
                                        }}
                                    </Column>
                                </Table>
                                <Divider />
                                <Pagination index={ index }  total={ total>1000?1000:total } size={ size } align='right' onChange={this.exchangePage}/>
                                <Divider />
                            </Row>
                        </Col>
                    </Row>
                </div>
                <Footer/>
            </Page>
        );
    }

}

NodeList.UIPage = page;
export default NodeList;
