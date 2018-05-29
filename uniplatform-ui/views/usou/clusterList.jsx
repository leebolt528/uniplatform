import React, {Component} from 'react';
import {
  Page, Row, Col, Table, Column, Divider, Button, Icon, Text, Pagination, context,
  Modal, ModalHeader, ModalBody, ModalFooter, Form, FormItem, Label, Input, Snackbar, popup,Dropdown
} from 'epm-ui';
import {SingleBanner, Footer, Chart, convertByteToGbUtil,Chartbar,StackedBarChart,COMM_HeadBanner} from '../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
  title: 'Usou',
  css: [
    'css/clusters.min.css',
    'css/singleBanner.min.css'
  ],
  js: []
};

class ClusterList extends Component {
  constructor(props) {
    super(props);
    this.state = {
        index: 1,
        index_table: 1,
        size: 5,
        size_table: 5,
        count: 0,
        count_table: 0,
        clusters: [],
        clusters_table: [],
        singleCluster: [],
        visible: false,
        updateVisible: false,
        manageVisible:false,
        heap:[],
        cpu:[],
        disk:[],
        indexStaData: [],
        documentStaData: [],
        storageStaData: [],
        clusterName:[],
        connectName:[],
        uri:[],
        health:[],
        clusterId:[],
        selectedData: null
      // clientHeight: window.screen.height
    }
    this.afterSubmit = this.afterSubmit.bind(this);
    this.beforeUpdateSubmit = this.beforeUpdateSubmit.bind(this);
    this.afterUpdateSubmit = this.afterUpdateSubmit.bind(this);
    this.handleSelect = this.handleSelect.bind( this );
  }

  componentDidMount() {
    this.fetchUsouClusterList();
    this.fetchUsouClusterStastics();
    this.fetchUsouClusterList_table();
  }

  fetchUsouClusterList() {
    let param = new FormData();
    let index = this.state.index - 1;
    let {size} = this.state;
    param.append('size', size);
    fetch(Uniplatform.context.url + '/usou/clusters/overview/' + index, {
      credentials: 'same-origin',
      method: 'POST',
      body: param
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.clusters.length > 0) {
          let {count, clusters} = data;
          this.setState({count, clusters});
          this.getSingleCluster();
        } else {
          popup(<Snackbar message="抱歉，暂时没有数据"/>);
        }
      }).catch((err) => console.log(err.toString()));

  }

    fetchUsouClusterList_table() {
        let param = new FormData();
        let index_table = this.state.index_table - 1;
        let {size_table} = this.state;
        param.append('size', size_table);
        fetch(Uniplatform.context.url + '/usou/clusters/overview/' + index_table, {
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res) => res.json())
            .then((data) => {
                if (data.clusters.length > 0) {
                    let {count, clusters} = data;
                    this.setState({count_table:count, clusters_table:clusters});
                    this.getSingleCluster_table();
                } else {
                    popup(<Snackbar message="抱歉，暂时没有数据"/>);
                }
            }).catch((err) => console.log(err.toString()));

    }

  exChangePagi(index_table, size_table) {
    this.setState({index_table, size_table}, () => this.fetchUsouClusterList_table());
  }

  getSingleCluster(){
    let {clusters} = this.state;
    let clusterName=[],connectName=[],uri=[],health=[],clusterId=[];
    clusters.map(function (index) {
        let cluster_name = index.clusterName;
        let connect_name = index.connectName;
        let _uri = index.uri;
        let _health = index.health;
        let _id=index.id;
        clusterName.push(cluster_name);
        connectName.push(connect_name);
        uri.push(_uri);
        health.push(_health);
        clusterId.push(_id);
    });
    this.setState({clusterName,connectName,uri,health,clusterId});
  }

    getSingleCluster_table(){
        let {clusters_table} = this.state;
        let clusterName_table=[],connectName_table=[],uri_table=[],health_table=[],clusterId_table=[];
        clusters_table.map(function (index) {
            let cluster_name = index.clusterName;
            let connect_name = index.connectName;
            let _uri = index.uri;
            let _health = index.health;
            let _id=index.id;
            clusterName_table.push(cluster_name);
            connectName_table.push(connect_name);
            uri_table.push(_uri);
            health_table.push(_health);
            clusterId_table.push(_id);
        });
        this.setState({clusterName_table,connectName_table,uri_table,health_table,clusterId_table});
    }

  deleteCluster(id) {
        if(id == '' || id.length == 0){
            popup( <Snackbar message="您未选择任何集群！" /> );
        }else {
            const confirmShow = confirm("确定要删除该集群?");
            if (!confirmShow) {
                return false;
            }
            let param = new FormData();
            param.append('ids', id);
            fetch(Uniplatform.context.url + '/usou/clusters/mulDelete', {
                credentials: 'same-origin',
                method: 'POST',
                body:param
            })
                .then((res) => res.json())
                .then((data) => {
                    data.status == '200' ? popup(<Snackbar message="删除成功"/>) : popup(<Snackbar
                        message={data.status}/>)
                    this.fetchUsouClusterList();
                    this.fetchUsouClusterList_table();
                }).catch((err) => console.log(err.toString()));
        }
  }

  getClusterDetail(id,name) {
    window.open('/usou/clusterDetail?id=' + id+'&name='+name,'_self');
  }

  afterSubmit(data) {
      data.status == '200' ? popup(<Snackbar message="添加成功"/>)+this.successAdd() : popup(<Snackbar message={data.status}/>);
      this.fetchUsouClusterList();
  }
  successAdd(){
      this.setState({visible: false});
      this.resetAdd();
  }
  showDddCluster() {
    this.setState({visible: true});
  }

  handleClose() {
      this.resetAdd();
      this.setState({visible: false});
  }

  fetchUsouSingleCluster(id) {
    fetch(Uniplatform.context.url + '/usou/clusters/get/' + id, {
      credentials: 'same-origin',
      method: 'POST'
    })
      .then((res) => res.json())
      .then((data) => {
        let {singleCluster} = data;
        this.setState({singleCluster});
      }).catch((err) => console.log(err.toString()));
  }

  afterUpdateSubmit(data) {
    data.status == '200' ? popup(<Snackbar message="修改成功"/>) + this.successUpdate() : popup(<Snackbar
      message={data.status}/>)
  }
  successUpdate(){
    this.resetUpdate();
    this.setState({updateVisible: false});
    this.fetchUsouClusterList();
  }
  beforeUpdateSubmit(data) {
    let id = 'id';
    data[id] = this.state.singleCluster.id;
    return data;
  }

  showUpdateCluster(id) {
    this.fetchUsouSingleCluster(id);
    this.setState({updateVisible: true});
  }

  handleUpdateClose() {
    this.resetUpdate();
    this.setState({updateVisible: false});
  }

  getClusterColor(color) {
    let result;
    switch (color) {
      case 'green' :
        result = '#27c627';
        break;
      case 'yellow':
        result = 'yellow';
        break
      case 'red' :
        result = '#c23934';
        break;
      default:
        result = '#aba4a4';
        break;
    }
    let colorDiv = <div style={{marginTop:'7px',background: result, width: '16px', height: '16px', display: 'inline-block', borderRadius: '8px'}}></div>;
    return colorDiv;
  }

  fetchUsouClusterStastics() {
    fetch(Uniplatform.context.url + '/usou/clusters/statistics', {
      credentials: 'same-origin',
      method: 'GET'
    })
      .then((res) => res.json())
      .then((data) => {
        let indexStaData = [];
        let documentStaData = [];
        let storageStaData = [];
        let heap = [];
        let cpu = [];
        let disk = [];
        data.data.map(function (item) {
            heap.push({"name":item.cluster,"low":item.indication_rate_count.heap.low,
                "middle":item.indication_rate_count.heap.middle,"high":item.indication_rate_count.heap.high,
                "top":item.indication_rate_count.heap.top});
            cpu.push({"name":item.cluster,"low":item.indication_rate_count.cpu.low,
                "middle":item.indication_rate_count.cpu.middle,"high":item.indication_rate_count.cpu.high,
                "top":item.indication_rate_count.cpu.top});
            disk.push({"name":item.cluster,"low":item.indication_rate_count.disk.low,
                "middle":item.indication_rate_count.disk.middle,"high":item.indication_rate_count.disk.high,
                "top":item.indication_rate_count.disk.top});
            let s = convertByteToGbUtil(item.size_of_store);
            let c = s.toString();
            let sizeStore = parseFloat(c);
          indexStaData.push({"name": item.cluster, "value": item.number_of_index ? item.number_of_index : 0});
          documentStaData.push({"name": item.cluster, "value": item.number_of_docs ? item.number_of_docs : 0});
          storageStaData.push({"name": item.cluster, "value": sizeStore ? sizeStore : 0});
        });
        this.setState({indexStaData, documentStaData, storageStaData,heap,cpu,disk});
        this.chart();
      }).catch((err) => console.log(err.toString()));
  }

  //更多
  MoreClusters(){
      let {clusters} = this.state;
      let index = this.state.index;
      let colomn = (clusters.length)/5;
      for(let i = 1; i< colomn;i++){
          index = index + 1;
      }

      let {size} = this.state;
      let param = new FormData();
      param.append('size', size);
      fetch(Uniplatform.context.url + '/usou/clusters/overview/' + index, {
          credentials: 'same-origin',
          method: 'POST',
          body: param
      })
          .then((res) => res.json())
          .then((data) => {
              if (data.clusters.length > 0) {
                  let {count} = data;
                  let newClusters = data.clusters;
                  let {clusters} = this.state;
                  for(let i=0;i<newClusters.length;i++){
                      clusters[clusters.length] = newClusters[i];
                  }
                  this.setState({count});
                  this.getSingleCluster();
                  this.onloadCol();
              } else {
                  popup(<Snackbar message="抱歉，暂时没有数据"/>);
              }
          }).catch((err) => console.log(err.toString()));

  }
    //更多——加载行
    onloadCol(){
        let {clusterName,connectName,uri,health,clusterId} = this.state;
        let tbodyHtml = [];
        let clusterNumber = ((clusterName.length)%5)==0 ? clusterName.length : clusterName.length+(5-((clusterName.length)%5));
        for(let i = 0;i<clusterNumber; i++) {
          if(clusterName[i]){
            let status = '';
            switch (health[i]) {
                case 'green' :
                    status = '良好';
                    break;
                case 'yellow':
                    status = '一般';
                    break;
                case 'red' :
                    status = '较差';
                    break;
                default:
                    status = '不可访问';
                    break;
            }
            tbodyHtml.push(
                <div className="cent" key={i}>
                    <h4 style={{fontWeight:'600'}}>{clusterName[i]}</h4>
                    <span>连接名：{connectName[i]}</span>
                    <span>地址:{uri[i]}</span>
                    <span>状态:{status}  {this.getClusterColor(health[i])}</span>

                    <div className="cs">
                    <Button type="default" size="tiny" onClick={this.showUpdateCluster.bind(this,clusterId[i])} style={{top:'35%'}}>
                        <Icon icon="edit"/> 编辑
                    </Button>
                    <Button type="default" size="tiny" onClick={this.deleteCluster.bind(this,clusterId[i])} style={{top:'35%'}}>
                        <Icon icon="trash-o"/> 删除
                    </Button>
                    {
                        status=="不可访问"?
                        <Button type="default" disabled size="tiny" style={{top:'35%',opacity:1}}>
                            <Icon icon="eye"/> 查看
                        </Button>
                        :
                        <Button type="default"  size="tiny" onClick={this.getClusterDetail.bind(this,clusterId[i],clusterName[i])} style={{top:'35%'}}>
                            <Icon icon="eye"/>查看
                        </Button>
                    }
                </div>
                </div>
            );
          }else {
              tbodyHtml.push(<Col style={{float:'right',width:'18%',margin:'1%',height:'120px',border:'1px solid rgb(216,221,230)',textAlign:'center'}} key={i}>
                <a type="default" size="tiny"
                   style={{float:'ri',margin: '10px auto', position: 'relative', zIndex: 1}}
                   onClick={this.showDddCluster.bind(this)}>
                  <Icon icon="plus" className="plus fa-4x" style={{margin:'30px 0px'}}/>
                </a>
              </Col>);
          }

        };
        return tbodyHtml;
    }

    //管理
    showManageCluster() {
        this.setState({manageVisible: true});
    }
    handleManageClose() {
        this.setState({manageVisible: false});
    }
    handleSelect( data ) {
        this.setState( { selectedData: data.map( ( key ) => key.id ).join( ',' ) } );
    }

    chart(){
        let {indexStaData,documentStaData,storageStaData,heap,cpu,disk} = this.state;
        let heapHtml = [],cpuHtml = [],diskHtml = [], indexStaDataHtml = [],
            docStaDataHtml = [],stoStaDataHtml = [];
        if(heap.length > 0){
            heapHtml.push(<Col size={{normal: 10, small: 24, medium: 10, large: 10}} offset={{small:0,medium:2,large:2}} key="">
                <h3 style={{color: '#3e8b68'}}>集群节点堆内存统计</h3>
                <StackedBarChart data={heap}/>
            </Col>);
        }
        if(cpu.length > 0){
            cpuHtml.push(<Col size={{normal: 10, small: 24, medium: 10, large: 10}} offset={{small:0,medium:2,large:2}} key="">
                <h3 style={{color: '#3e8b68'}}>集群节点CPU统计</h3>
                <StackedBarChart data={cpu}/>
            </Col>);
        }
        if(disk.length > 0){
            diskHtml.push(<Col size={{normal: 10, small: 24, medium: 10, large: 10}} offset={{small:0,medium:2,large:2}} key="">
                <h3 style={{color: '#3e8b68'}}>集群节点硬盘统计</h3>
                <StackedBarChart data={disk}/>
            </Col>);
        }
        if(indexStaData.length > 0){
            indexStaDataHtml.push(<Col size={{normal: 10, small: 24, medium: 10, large: 10}} offset={{small:0,medium:2,large:2}} key="">
                <h3 style={{color: '#3e8b68'}}>集群索引统计</h3>
                <Chartbar data={indexStaData}/>
            </Col>);
        }
        if(documentStaData.length > 0){
            docStaDataHtml.push(<Col size={{normal: 10, small: 24, medium: 10, large: 10}} offset={{small:0,medium:2,large:2}} key="">
                <h3 style={{color: '#3e8b68'}}>集群文档统计</h3>
                <Chartbar data={documentStaData}/>
            </Col>);
        }
        if(storageStaData.length > 0){
            stoStaDataHtml.push(<Col size={{normal: 10, small: 24, medium: 10, large: 10}} offset={{small:0,medium:2,large:2}} key="">
                <h3 style={{color: '#3e8b68'}}>集群存储统计(GB)</h3>
                <Chartbar data={storageStaData}/>
            </Col>);
        }
        this.setState({heapHtml,cpuHtml,diskHtml,indexStaDataHtml,docStaDataHtml,stoStaDataHtml});
    }
    onChangeUrl(){
        let bool=true;
        let valueAll=document.getElementsByClassName('inputUrl')[0].firstChild.value;
        if(valueAll.length==0){
            document.getElementsByClassName('inputUrl')[0].nextSibling.style.display="none";
        }
        let regUrl=new RegExp();
        regUrl.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\:"
            +"([1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]]|[1-9]\\d{3}|[1-9]\\d{2}|[1-9]\\d|\\d)$");
            if(valueAll.length>0){
                valueAll.split(',').map((value)=>{
                if(!regUrl.test(value)){
                    bool=false;
                }
            });
            }
      if(bool==false){
          document.getElementsByClassName('inputUrl')[0].firstChild.classList.add('inputUrlChild');
          document.getElementsByClassName('inputUrl')[0].nextSibling.style.display="inline-block";
      }else{
          document.getElementsByClassName('inputUrl')[0].firstChild.classList.remove('inputUrlChild');
          document.getElementsByClassName('inputUrl')[0].nextSibling.style.display="none";
      }
    }
    onChangeUrlAdd(){
        let bool=true;
        let valueAll=document.getElementsByClassName('inputUrlAdd')[0].firstChild.value;
        if(valueAll.length==0){
            document.getElementsByClassName('inputUrlAdd')[0].nextSibling.style.display="none";
        }
        let regUrl=new RegExp();
        regUrl.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\:"
            +"([1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]]|[1-9]\\d{3}|[1-9]\\d{2}|[1-9]\\d|\\d)$");
            if(valueAll.length>0){
                valueAll.split(',').map((value)=>{
                if(!regUrl.test(value)){
                    bool=false;
                }
            });
            }
      if(bool==false){
          document.getElementsByClassName('inputUrlAdd')[0].firstChild.classList.add('inputUrlChild');
          document.getElementsByClassName('inputUrlAdd')[0].nextSibling.style.display="inline-block";
      }else{
          document.getElementsByClassName('inputUrlAdd')[0].firstChild.classList.remove('inputUrlChild');
          document.getElementsByClassName('inputUrlAdd')[0].nextSibling.style.display="none";
      }
    }
    //清空回显
    formTirggerAdd( trigger ) {
        this.resetAdd = trigger.reset;
    }
    formTirggerUpdate( trigger ) {
        this.resetUpdate = trigger.reset;
    }
  render() {
    let {clusters,tbodyHtml,clusterName,connectName,uri,health,singleCluster,visible,index,count,size,updateVisible,
        manageVisible,selectedData,heapHtml,cpuHtml,diskHtml,indexStaDataHtml,docStaDataHtml,stoStaDataHtml,
        size_table,index_table,count_table,clusters_table,clusterName_table,connectName_table,uri_table,health_table,clusterId_table} = this.state;
    // let clientHeight = window.screen.height;

    return (
      <Page>
        {/*<SingleBanner prefix="搜索引擎" id={''}/>*/}
          <COMM_HeadBanner prefix="搜索引擎"/>
          <Divider/>
        <Row style={{minHeight: '600px'}}>
          <Row>
            <Col size={{normal: 24, small: 24, medium: 24, large: 24}}>
            </Col>
            {/*<Col style={{width:'18%',margin:'1%',height:'120px',backgroundColor:'white',opacity:0.5,zIndex:2,position:'absolute'}}>
                {(value) => {
                    return (
                        <div>
                          <Button type="default" size="tiny" onClick={this.showUpdateCluster.bind(this, value.id)}>
                            <Icon type="edit"/> 编辑
                          </Button>
                          <Button type="default" size="tiny" onClick={this.deleteCluster.bind(this, value.id)}>
                            <Icon type="trash-o"/> 删除
                          </Button>
                          <Button type="default" size="tiny" onClick={this.getClusterDetail.bind(this, value.id)}>
                            <Icon type="eye"/> 查看
                          </Button>
                        </div>
                    )
                }}
            </Col>*/}
              { this.onloadCol() }
              {tbodyHtml}
          </Row>
          <Row>
            <Col size={{normal: 24, small: 24, medium: 24, large: 24}}>
              <Button type="default" onClick={this.showDddCluster.bind(this)}
                      style={{float:'ri',margin:'10px auto',position:'relative',zIndex:1,width:'32%'}}>
                 创建 <Icon icon="plus-square-o"/>
              </Button>
              <Button type="default" onClick={this.MoreClusters.bind(this)}
                      style={{float:'ri',margin:'10px 2%',position:'relative',zIndex:1,width:'32%'}}>
                更多 <Icon icon="plus-square-o"/>
              </Button>
              <Button type="default" onClick={this.showManageCluster.bind(this)}
                      style={{float:'ri',margin:'10px auto',position:'relative',zIndex:1,width:'32%'}}>
                管理 <Icon icon="plus-square-o"/>
              </Button>
              <br/>

            </Col>
          </Row>

          <Row>
              {heapHtml}
              {cpuHtml}
              {diskHtml}
              {indexStaDataHtml}
              {docStaDataHtml}
              {stoStaDataHtml}
          </Row>
        </Row>

        <Form type="horizontal"
            method="post"
            action={contextUrl + '/usou/clusters/save'}
            async={true}
            onAfterSubmit={this.afterSubmit}
            trigger={ this.formTirggerAdd.bind(this) }
            >
            {
                visible?
                <Modal onClose={this.handleClose.bind(this)}>
                    <ModalHeader>
                    添加集群
                    </ModalHeader>
                    <ModalBody>
                    <FormItem required={true}>
                        <Label>连接名</Label>
                        <Input name="connectName" placeholder="请输入连接名"/>
                    </FormItem>
                    <FormItem required={true}>
                        <Label>地址栏</Label>
                            <Input name="uri" placeholder="例如:127.0.0.1:8000,121.0.0.1:8080" className="inputUrlAdd" onChange={this.onChangeUrlAdd.bind(this)}/>
                            <div className="error-text" style={{display:'none',color:'#c23934',fontSize:'0.75rem',marginTop:'0.5rem'}}>请输入正确内容</div>
                        </FormItem>
                    <FormItem required={true}>
                        <Label>集群名</Label>
                        <Input name="clusterName" placeholder="请输入集群名"/>
                    </FormItem>
                    <FormItem>
                        <Label>用户名</Label>
                        <Input name="userName" placeholder="请输入用户名"/>
                    </FormItem>
                    <FormItem>
                        <Label>密码</Label>
                        <Input name="password" type="password" placeholder="请输入密码"/>
                    </FormItem>
                    </ModalBody>
                    <ModalFooter>
                    <Button onClick={this.handleClose.bind(this)}>关闭</Button>
                    <Button type="primary" htmlType="submit">确定</Button>
                    </ModalFooter>
                </Modal>
                :
                null
            }
      </Form>

      <Form type="horizontal"
        method="post"
        action={contextUrl + '/usou/clusters/update'}
        async={true}
        onSubmit={this.beforeUpdateSubmit}
        onAfterSubmit={this.afterUpdateSubmit}
        trigger={ this.formTirggerUpdate.bind(this) }
        >
        {
            updateVisible?
            <Modal onClose={this.handleUpdateClose.bind(this)}>
                <ModalHeader>
                修改集群
                </ModalHeader>
                <ModalBody>
                <FormItem required={true}>
                    <Label>连接名</Label>
                    <Input name="connectName" value={singleCluster ? singleCluster.connectName : null} placeholder="必填项"/>
                </FormItem>
                <FormItem required={true}>
                    <Label>地址栏</Label>
                        <Input name="uri" value={singleCluster ? singleCluster.uri : null} placeholder="必填项" className="inputUrl" onChange={this.onChangeUrl.bind(this)}/>
                        <div className="error-text" style={{display:'none',color:'#c23934',fontSize:'0.75rem',marginTop:'0.5rem'}}>请输入正确内容</div>
                </FormItem>
                <FormItem required={true}>
                    <Label>集群名</Label>
                    <Input name="clusterName" value={singleCluster ? singleCluster.clusterName : null} placeholder="必填项"/>
                </FormItem>
                <FormItem>
                    <Label>用户名</Label>
                    <Input name="userName" value={singleCluster ? singleCluster.userName : null} placeholder="请输入"/>
                </FormItem>
                <FormItem>
                    <Label>密码</Label>
                    <Input name="password" value={singleCluster ? singleCluster.password : null} placeholder="请输入"/>
                </FormItem>
                </ModalBody>
                <ModalFooter>
                <Button onClick={this.handleUpdateClose.bind(this)}>关闭</Button>
                <Button type="primary" htmlType="submit">确定</Button>
                </ModalFooter>
            </Modal>
            :
            null
        }
      </Form>

          {this.state.manageVisible ?
          <Modal onClose={this.handleManageClose.bind(this)}>
              <ModalHeader>
                  管理集群
              </ModalHeader>
              <ModalBody>
                  <Button type="default" size="tiny" onClick={this.deleteCluster.bind(this,this.state.selectedData)}>
                      <Icon icon="trash-o"/> 删除
                  </Button>
                  <br/>
                  <Table className="modalTable" dataSource={clusters_table} striped={true} multiLine={false} bodyScrollable={ false } bodyHeight={ '200px' } checkable headBolder
                         onCheck={ this.handleSelect }>
                      <Column dataIndex="connectName" title="连接名" scaleWidth='25%' textAlign="center"/>
                      <Column title="地址栏" scaleWidth='30%' textAlign="center" >
                            {(value) => {
                                const a = <Dropdown position={'right'} key={index}>
                                    <Dropdown.Trigger action="hover">
                                        <span style={{overflow:'hidden',textOverflow:'ellipsis',whiteSpace:'nowrap',width:'100%',display:'inline-block'}}>{value.uri}</span>
                                    </Dropdown.Trigger>
                                    <Dropdown.Content>
                                    {
                                        <div style={ { maxWidth: '240px',maxHeight:'200px',overflowY:'auto'} }>
                                            {
                                                value.uri.split(',').map((value,index)=>{
                                                    return (<p key={index} style={{marginLeft:'20px',marginRight:'20px'}}>{value}</p>);
                                                })
                                            }
                                        </div>
                                    }
                                    </Dropdown.Content>
                                </Dropdown>
                            return a;
                        }}
                      </Column>
                      <Column dataIndex="clusterName" title="集群名" scaleWidth='30%' textAlign="center"/>
                      <Column title="状态" textAlign="center" scaleWidth='10%'>
                          {(value) => {
                              let result = this.getClusterColor(value.health);
                              return result;
                          }}
                      </Column>
                  </Table>
                  <Divider/>
                  <Pagination align='right' index={index_table} total={count_table>1000?1000:count_table } size={size_table} onChange={this.exChangePagi.bind(this)}/>
              </ModalBody>
              <ModalFooter>
                  <Button onClick={ this.handleManageClose.bind( this ) }>关闭</Button>
              </ModalFooter>
          </Modal>
          : ''}

        <Footer/>
      </Page>
    );
  }

}

ClusterList.UIPage = page;
export default ClusterList;
