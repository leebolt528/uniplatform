import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Upload,
    D3Tree,Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '文本聚类',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ]
};

class TextClustering extends Component {
    constructor(props){
        super(props);

        this.state = {
            clusterTree: {},
            visible: false
        }
    }

    componentDidMount() {
    }

    formGetterLanguage( getter ) {
        this.getValueSource = getter.value;
    }

    //文本路径检索
    pathSearch(event){
        if (event.which==13){
            let keyWord = this.getValue();
            this.setState({keyWord}, () => this.handleValue());
        }
    }
    getInput(getter){
        this.getValue=getter.value;
    }
    /*wordSearchAuto(data){
        if(data == ''){
            this.setState({ keyWord : '' }, () => this.handleValue());
        }
    }*/

    handleValue() {
        const flag = this.getValueSource();
        const text = this.getValue();
        if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写文本路径"/>);
            return;
        }
        this.setState({show:true});
        let param = new FormData();
        param.append('textPath', text);
        if(flag == 'chinese'){
            fetch(Uniplatform.context.url + '/nlap/admin/text/cluster/clustering', {
                credentials: 'same-origin',
                method: 'POST',
                body: param
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                    }else if (data.status != 200){
                        popup(<Snackbar message="聚类失败"/>);
                        this.setState({show:false});
                    }
                    let number = 0;
                    let children = data.data.map(function (item) {
                        number += item.docNum;
                        let clu;
                        if(item.childrenCluster.length == 0){
                            clu = {name: item.parentCluster + ':' + item.docNum};
                        }else {
                            let chiclu = item.childrenCluster.map(function (chi) {
                                let chi_clu = {name: chi.cluster + ':' + chi.docNum};
                                return chi_clu;
                            });
                            clu = {
                                name: item.parentCluster + ':' + item.docNum,
                                children: chiclu
                            };
                        }
                        return clu;
                    });
                    let clusterTree = {
                        name: '聚类：'+number,
                        children: children
                    };
                    this.setState({clusterTree,show:false,article:data.data});
                    this.showArticle();
                    console.log(clusterTree)
                }).catch((err) => console.log(err.toString()));
        }else {
            fetch(Uniplatform.context.url + '/nlap/admin/text/cluster/clustering/en', {
                credentials: 'same-origin',
                method: 'POST',
                body: param
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                    }else if (data.status != 200){
                        popup(<Snackbar message="聚类失败"/>);
                        this.setState({show:false});
                    }
                    let number = 0;
                    let children = data.data.map(function (item) {
                        number += item.docNum;
                        let clu;
                        if(item.childrenCluster.length == 0){
                            clu = {name: item.parentCluster + ':' + item.docNum};
                        }else {
                            let chiclu = item.childrenCluster.map(function (chi) {
                                let chi_clu = {name: chi.cluster + ':' + chi.docNum};
                                return chi_clu;
                            });
                            clu = {
                                name: item.parentCluster + ':' + item.docNum,
                                children: chiclu
                            };
                        }
                        return clu;
                    });
                    let clusterTree = {
                        name: '聚类：'+number,
                        children: children
                    };
                    this.setState({clusterTree,show:false,article:data.data});
                    this.showArticle();
                    console.log(clusterTree)
                }).catch((err) => console.log(err.toString()));
        }
    }

    showArticle(){
        let {article} = this.state;
        let articleHtml = [];
        for(let i=0;i<article.length;i++){
            articleHtml.push(
                <div style={{display:'inline-block',backgroundColor:'#777777',color:'#fff',borderRadius:'5px',
                    margin:'4px',padding:'2px'}} onClick={this.detailModal.bind(this,article[i].parentCluster)} key={i}>
                    {article[i].parentCluster} : {article[i].docNum}
                </div>
            );
        }
        this.setState({articleHtml});
    }

    detailModal(parentCluster){
        let {article} = this.state;
        let articleList = [];
        article.map(function (item) {
            if(item.parentCluster == parentCluster){
                item.doclist.map(function (doc) {
                    articleList.push({
                        doclist : doc
                    });
                });
            }
        });
        this.setState( { visible: true,articleList } );
    }

    handleClose() {
        this.setState( { visible: false } );
    }



    render() {
        let {clusterTree,show,articleHtml,visible,articleList} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'950px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/textClustering'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in9.png"/><span className="classTitle">文本聚类</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px',height:'100px'}}>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                <Label>文本路径:</Label>
                                <Input placeholder="请输入文本路径" type="search"
                                       onKeyPress={ this.pathSearch.bind(this)}
                                       getter={ this.getInput.bind(this) }
                                       //onChange={this.wordSearchAuto.bind(this)}
                                       style={{width:'20%',display:'inline-block'}}>
                                    <Input.Left icon="search"/>
                                </Input>
                                <Select value="chinese" showClear={false} getter={ this.formGetterLanguage.bind(this) }
                                        style={{width:'20%',display:'inline-block',margin:'2px 10px'}}>
                                    <Option value="chinese">中文</Option>
                                    <Option value="english">英文</Option>
                                </Select>
                                <Button type="info" style={{margin:'0 20px'}} onClick={this.handleValue.bind(this)}>聚类</Button>
                            </Col>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">聚类结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content" style={{maxHeight:'720px',overflow:'auto'}}>
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'80%'}}>
                                    {show ? <Loading /> : null}
                                    <D3Tree
                                        dataSource={ clusterTree }
                                        zoom={ true }
                                    />
                                    <div>{articleHtml}</div>
                                </Col>
                            </div>
                        </Row>
                    </Col>
                </Row>
                <Modal visible={ visible } onClose={ this.handleClose.bind( this ) } >
                    <ModalHeader>
                        文章详细列表
                    </ModalHeader>
                    <ModalBody>
                        <div style={{margin:'20px'}}>
                            <Table striped={true} multiLine={true} dataSource={articleList} bordered>
                                <Column title="序号" scaleWidth = '40%' textAlign="center">
                                    { ( value, index ) => { return Number(index) + 1; } }
                                </Column>
                                <Column dataIndex="doclist" title="文件名" scaleWidth='60%' textAlign="center"/>
                            </Table>
                        </div>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleClose.bind( this ) }>关闭</Button>
                    </ModalFooter>
                </Modal>

                <Footer />
            </Page>
        );
    }

}
TextClustering.UIPage = page;
export default TextClustering;