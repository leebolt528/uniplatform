import React, { Component } from 'react';
import {Page,Row,Col,ButtonGroup,Button,Icon,Table,Column,Text,Pagination,Select,
    Form,FormItem,Label,Option,Divider,Modal, ModalHeader, ModalBody} from 'epm-ui';
import { SingleBanner,util_bytesToSize,Footer,COMM_HeadBanner,UsouMenu,NavigationBar} from '../../components/uniplatform-ui';

const page = {
    title: 'Usou',
    css: [
        'css/clusters.min.css',
        'css/singleBanner.min.css',
        'css/jquery.json-viewer.min.css',
        'css/leftnav.min.css'
    ],
    js: [
        'js/jquery.json-viewer.min.js'
    ]
};

const unshardStyle = {
    float: 'left',
    lineHeight: '30px',
    color: '#6B6969',
    border: '1px solid #ccc',
    width: '30px',
    height: '30px',
    background: '#ECECEB',
    margin: 'auto 5px',
};
const getConstructorShard = (shardData) => {
    let shardAssign = {
        primary : shardData.primary,
        shard : shardData.shard,
        state : shardData.state,
        node : shardData.node,
        index : shardData.index,
        id : shardData.node + '_' + shardData.shard + '_' + shardData.index
    }
    return shardAssign;
};

class ClusterDetail extends Component {
    constructor(props){
        super(props);
        this.state = {
            index:1,
            size:4,
            total:0,
            aliases:[],
            health:[],
            indexStats:[],
            nodes:[],
            nodesStats:[],
            state:[],
            clusterRow:[],
            clusterCol:[],
            totalData:[],
            refresTime:'10000',
            visible: false,
            indices : '',
            shardsIndex : '',
            shardDataIndex:[]
        }
        this.fetchClusterDetail = this.fetchClusterDetail.bind(this);
        this.getNodeDeail = this.getNodeDeail.bind(this);
        this.exchangePage =  this.exchangePage.bind(this);
        this.beforeSubmit = this.beforeSubmit.bind(this);
    }

    componentDidMount() {
       this.fetchClusterDetail();
    }

    fetchClusterDetail(){
        // let { index,size,refresTime } = this.state;
        // let timer = setTimeout(() => {
        //     fetch( Uniplatform.context.url + '/usou/cluster/' + this.props.page.id, {
        //             credentials: 'same-origin',
        //             method: 'POST'
        //         } )
        //         .then( ( res ) => res.json())
        //         .then( ( data ) => {
        //             let { aliases,health, indexStats,nodes, nodesStats, state } = data;
        //             this.setState( {aliases, health, indexStats,nodes, nodesStats, state } );
        //             this.getCluster();
        //             this.exchangePage(this.state.index,size)
        //         }).catch( ( err ) => console.log( err.toString() ) );
        //     clearTimeout(timer)
        //     this.createTimer();
        // }, refresTime)
        fetch( Uniplatform.context.url + '/usou/cluster/' + this.props.page.id, {
            credentials: 'same-origin',
            method: 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let { index,size } = this.state;
                let { aliases,health, indexStats,nodes, nodesStats, state } = data;
                this.setState( {aliases, health, indexStats,nodes, nodesStats, state } );
                this.getCluster();
                this.exchangePage(index,size)
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    // 创建定时器
    createTimer(){
        this.fetchClusterDetail();
    }

    beforeSubmit(data){
        let refresTime = data;
        this.setState( { refresTime }, () => this.fetchClusterDetail() );
    }
    getClusterColor(color){
        let result;
        switch(color){
            case 'green' : result = '#27c627';
                break;
            case 'yellow': result = 'yellow';
                break;
            case 'red' : result = '#c23934';
                break;
            default: result = '#aba4a4';
                break;
        }
        let colorDiv = <div style={{background:result,width:'16px',height:'16px',display:'inline-block',borderRadius:'8px'}}></div>;
        return colorDiv;
    }
    getNumberNodes(nodes){
        return nodes.hasOwnProperty( "nodes") ? Object.keys(nodes.nodes).length : '0';
    }
    getNumberIndices(indexStats){
        return indexStats.hasOwnProperty( "indices") ? Object.keys(indexStats.indices).length : '0';
    }
    getNumberShards(health){
        let countShands = health.active_shards + health.relocating_shards +
            health.unassigned_shards + health.initializing_shards;
        return countShands > 0 ? countShands : 0;
    }
    getNumberDocs(indexStats){
        return indexStats.hasOwnProperty( "_all") ? indexStats._all.primaries.docs.count : '0';
    }
    getNumberSize(indexStats){
        return util_bytesToSize(indexStats.hasOwnProperty( "_all") ? indexStats._all.total.store.size_in_bytes : '0');
    }

    /* 列信息 */
    getCountNodes(){
        let {nodes,nodesStats,state} = this.state;
        if(nodes.hasOwnProperty( "nodes")){
             let innerShard = this.getCountShards().shards;
             let cluster_nodes = Object.keys(nodes.nodes).map(function(nodeId) {
                 let nodeAssign = {
                     nodeId : nodeId,
                     nodeStats : nodesStats.nodes[nodeId],
                     nodeInfo : nodes.nodes[nodeId],
                     nodeMaster : nodeId === state.master_node ? "realstar" : "emptystar"
                 };
                 let column_nodes = {
                     columnNode : nodeAssign
                 };
                 let all_column = [];
                 var indicesNames = Object.keys(state.routing_table.indices);
                 indicesNames.map(function(indexName) {
                     all_column.push(indexName);
                 });
                 Object.keys(all_column).map(function(indexColumn){
                     let shardName = all_column[indexColumn];
                     column_nodes[shardName] = nodeId + "_" + shardName;
                 });
                 return column_nodes;
            });

            let columnFirst = {
                nodeInfo : '未分配'
            };
            let firstCol = {
                columnNode : columnFirst
            };
            let all_indices = [];
            Object.keys(state.routing_table.indices).map(function(indexName) {
                all_indices.push(indexName);
            });
            Object.keys(all_indices).map(function(indexColumn){
                let shardName = all_indices[indexColumn];
                firstCol[shardName] = shardName;
            });
            cluster_nodes.unshift(firstCol);
            return cluster_nodes;
         }
    }
    /* 分片信息 */
    getCountShards(){
        let {state} = this.state;
        let shards = {},
            unassignedShards = {};
        let indicesNames = Object.keys(state.routing_table.indices);
        let indicesRouting = state.routing_table.indices;
        indicesNames.forEach(function(indexName) {
            let totalShards = Object.keys(indicesRouting[indexName].shards);
            totalShards.forEach(function(shardNum) {
                indicesRouting[indexName].shards[shardNum].forEach(function(shardData) {
                    if (shardData.state === 'UNASSIGNED') {
                        if (!unassignedShards.hasOwnProperty(shardData.index)) {
                            unassignedShards[shardData.index] = [];
                        }
                        let shardAssign = getConstructorShard(shardData);
                        unassignedShards[shardData.index].push(shardAssign);
                    } else {
                        let shardAssign = getConstructorShard(shardData);
                        let key = shardAssign.node + '_' + shardAssign.index;
                        if(!shards.hasOwnProperty(key)){
                            shards[key] = [];
                        }
                        shards[key].push(shardAssign);
                    }
                });
            });
        });
        let cluster_shards = {
            shards:shards,
            unassignedShards:unassignedShards
        }
        return cluster_shards;
    }
    getNodeDeail(nodes,nodeMaster,nodeStats){
        if(nodes == '未分配'){
            return '未分配分片';
        }
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
                search_icon = <Icon icon="search"/>;
            }
        }else{
            if(nodeMaster == "realstar"){
                real_icon = <Icon icon="star"/>;
            }
            // else{
            //     emp_icon = <Icon type="star-o"/>;
            // }
            search_icon = <Icon icon="search"/>;
        }
        let usebyte = (nodeStats.fs.total.total_in_bytes) - (nodeStats.fs.total.free_in_bytes),
            usePercent = (usebyte / (nodeStats.fs.total.total_in_bytes) ).toFixed(2);
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
                <p>heap:{nodeStats.jvm.mem.heap_used_percent}%  disk:{usePercent}</p>
                <p>cpu:{nodeStats.os.cpu_percent > 0 ? nodeStats.os.cpu_percent : 0}% load: {nodeStats.os.load_average > 0 ? nodeStats.os.load_average : 0}</p>
            </div>
        </div>;
        return detail;
    }
    /* 表头信息 */
    getCountIndices(){
        let {state,aliases,indexStats} = this.state;
        if(state.hasOwnProperty( "routing_table")){
            let innerShard = this.getCountShards().shards;
            let unShards = this.getCountShards().unassignedShards;
            var indicesNames = Object.keys(state.routing_table.indices);
            let cluster_indices = indicesNames.map((indexName) => {
                let allShards = Object.keys(state.routing_table.indices[indexName].shards).length,
                    firstShard = Object.keys(state.routing_table.indices[indexName].shards[0]).length -1,
                    docs = indexStats.indices[indexName].total.docs.count,
                    bytes = util_bytesToSize(indexStats.indices[indexName].total.store.size_in_bytes);
                let indicesAssign = {
                    title : <div style={{color: 'rgb(10, 138, 29)'}}>
                        <p>{indexName}</p>
                        <p>shards: {allShards}*{firstShard} | docs: {docs}</p>
                        <p>size: {bytes}</p>
                        </div>,
                    indexStats : indexStats.indices[indexName],
                    indexAliases : aliases[indexName],
                    dataIndex : indexName,
                    textAlign: 'center',
                    sub:(value) => {
                        let result = [];
                        if(value.indexOf('_') > -1){
                            Object.keys(innerShard).map((indexShard) => {
                                if(value === indexShard){
                                    Object.keys(innerShard[indexShard]).map((assignIndex) => {
                                        let primary = innerShard[indexShard][assignIndex].primary;
                                        result.push(<div className="shardStyle" style={{background: `${ primary?'rgb(43,111,18)':'#5ba341'}`}} key={assignIndex} onClick={this.getShardsDetail.bind(this,this.props.page.id,indexName,assignIndex)}>{innerShard[indexShard][assignIndex].shard}</div>);
                                    });
                                }
                            });
                        }else{
                            if(Object.keys(unShards).length > 0){
                                Object.keys(unShards).map((indexShard) => {
                                    if(value === indexShard){
                                        Object.keys(unShards[indexShard]).map((assignIndex) => {
                                            result.push(<div style={unshardStyle} key={assignIndex}>{unShards[indexShard][assignIndex].shard}</div>);
                                        });
                                    }
                                });
                            }
                        }

                        return result;
                    }
                };
                return indicesAssign;
            });
            return cluster_indices;
        }
    }

    getCluster(){
       let {clusterRow,totalData}  = this.state;
        let unShards = Object.keys(this.getCountShards().unassignedShards);
        clusterRow = this.getCountNodes();
        if(unShards.length <= 0){
            clusterRow.shift();
        }
        totalData = this.getCountIndices();
        this.setState({clusterRow,totalData});
    }

    exchangePage(index,size){
        let clusterCol = [];
        let totalData = this.state.totalData;
        let total = totalData.length;
        this.setState({index,size,total});
        for(let i = (index-1) * size; i < index * size; i++){
            if(totalData[i]){
                clusterCol.push(totalData[i])
            }
        }
        let firstNode = {
            title : '节点 / 索引',
            dataIndex : 'columnNode',
            textAlign: 'center',
            sub:  (value) => {
                let result = this.getNodeDeail(value.nodeInfo,value.nodeMaster,value.nodeStats);
                return result;
            }
        }
        clusterCol.unshift(firstNode);
        this.setState({clusterCol});
    }

    getOpenNode(){
        window.open(`/usou/nodeList?id=${this.props.page.id}&name=${this.props.page.name}`,'_self');
    }

    getOpenIndices(){
        window.open(`/usou/indexManage?id=${this.props.page.id}&name=${this.props.page.name}`,'_self');
    }
    getShardsDetail(id,indicesIndex,shards){
        let indices = indicesIndex;
        let shardsIndex = shards;
        let visible = true;
        this.setState({ visible ,indices ,shardsIndex});
        fetch(`${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/${indices}/${shardsIndex}`, {
            credentials: 'same-origin',
            method: 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( shardData ) => {
                let shardDataIndex = shardData.data[0];
                this.setState( { shardDataIndex } );
                $('#json-renderer').jsonViewer(shardDataIndex, '');
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    handleClose() {
        this.setState( { visible: false } );
    }
    render() {
        let {indices,shardsIndex,refresTime,health,nodes,indexStats,clusterRow,clusterCol,index,size,total,visible} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                <Divider />
                <Row>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <UsouMenu url={'/usou/clusterDetail'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }}>
                        <Row>
                            <NavigationBar code={'clusterDetail'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name}/>
                        </Row>
                        <Row>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="cluster_detail_col">
                                <div className = "cluster_detail_title" >
                                    <span className = "cluster_detail_span"> <Icon icon="bar-chart" /> 集群统计信息</span>
                                </div>
                            </Col>
                            <div className="cluster_detail_content">
                                <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }} className="cluster_detail_content_col">
                            <span onClick={this.getOpenNode.bind(this)} style={{cursor:'pointer',color: '#0070d2'}}>
                                <Text> { this.getNumberNodes(nodes) }</Text>
                                <Text content={ '节点' } ></Text>
                            </span>
                                </Col>
                                <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }} className="cluster_detail_content_col">
                            <span onClick={this.getOpenIndices.bind(this)} style={{cursor:'pointer',color: '#0070d2'}}>
                                <Text>{ this.getNumberIndices(indexStats) }</Text>
                                <Text content={ '索引' }></Text>
                            </span>
                                </Col>
                                <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }} className="cluster_detail_content_col">
                                    <Text>{ this.getNumberShards(health) }</Text>
                                    <Text content={ '分片' }></Text>
                                </Col>
                                <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }} className="cluster_detail_content_col">
                                    <Text>{ this.getNumberDocs(indexStats) }</Text>
                                    <Text content={ '文档' }></Text>
                                </Col>
                                <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }} className="cluster_detail_content_col">
                                    <Text >{ this.getNumberSize(indexStats) }</Text>
                                    <Text content={ '存储容量' }></Text>
                                </Col>
                            </div>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="cluster_detail_col">
                                <div className = "cluster_detail_title" >
                                    <span className = "cluster_detail_span"> <Icon icon="ambulance" /> 集群健康状态</span>
                                </div>
                            </Col>
                            <div className="cluster_detail_content">
                                <table className="epm bordered table">
                                    <tbody>
                                    <tr>
                                        <td>状态</td>
                                        <td><Text textAlign="center">{ this.getClusterColor(health.status) }</Text></td>
                                        <td>连接超时</td>
                                        <td><Text textAlign="center">{ String(health.timed_out) }</Text></td>
                                        <td>节点</td>
                                        <td><Text textAlign="center">{ health.number_of_nodes }</Text></td>
                                    </tr>
                                    <tr>
                                        <td>数据节点</td>
                                        <td><Text textAlign="center">{ health.number_of_data_nodes }</Text></td>
                                        <td>活动主分片</td>
                                        <td><Text textAlign="center">{ health.active_primary_shards}</Text></td>
                                        <td>活动分片</td>
                                        <td><Text textAlign="center">{ health.active_shards }</Text></td>
                                    </tr>
                                    <tr>
                                        <td>重分配分片</td>
                                        <td><Text textAlign="center">{ health.relocating_shards }</Text></td>
                                        <td>初始化分片</td>
                                        <td><Text textAlign="center">{ health.initializing_shards }</Text></td>
                                        <td>未分配分片</td>
                                        <td><Text textAlign="center">{ health.unassigned_shards }</Text></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="cluster_detail_col">
                                <div className = "cluster_detail_title" >
                                    <span className = "cluster_detail_span"> <Icon icon="credit-card-alt" /> 集群详细信息</span>
                                </div>
                            </Col>
                            <div className="cluster_detail_content">
                                <Pagination  index={ index }  total={ total>1000?1000:total } size={ size } pages={size} align='right' onChange={this.exchangePage}/>
                                <Table dataSource={clusterRow} columns={clusterCol} striped={ true } multiLine={ true } bordered={ true } />
                            </div>
                        </Row>
                    </Col>
                </Row>


                <Modal visible={ visible } onClose={ this.handleClose.bind( this ) } >
                    <ModalHeader>
                        {indices} 分片 {shardsIndex} 的状态
                    </ModalHeader>
                    <ModalBody>
                        <Row style={{maxHeight:'450px',overflow:'auto'}}>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                <div id="json-renderer"></div>
                            </Col>
                        </Row>
                    </ModalBody>
                </Modal>
                <Footer />
            </Page>
        );
    }

}

ClusterDetail.UIPage = page;
export default ClusterDetail;
