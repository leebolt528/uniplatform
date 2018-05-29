import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Upload,
    Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '相关词',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css',
        '../css/relation.min.css'
    ],
    js: [
        '../js/d3.v3.min.js',
        '../js/relation.min.js'
    ]
};

class RelatedWords extends Component {
    constructor(props){
        super(props);

        this.state = {
        }
    }

    componentDidMount() {
    }

    search(){
        let keyWord = this.getValueWord();
        let modelName = this.getValueModel();
        if(!modelName.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请输入模型名称"/>);
            return;
        }else if(!keyWord.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请输入关键字"/>);
            return;
        }
        this.setState({keyWord,modelName}, () => this.handleValue());
    }
    getInput(getter){
        this.getValueWord=getter.value;
    }
    getModel(getter){
        this.getValueModel=getter.value;
    }

    handleValue() {
        let show = true;
        this.setState({show});
        let {keyWord,modelName} = this.state;
        let param = new FormData();
        param.append('modelName', modelName);
        param.append('word', keyWord);
        fetch(Uniplatform.context.url + '/nlap/admin/relatedwords/related', {
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let relations = [];
                this.setState({relations});
                let seData = data;
                data.relations.map(function (item) {
                    relations.push({
                        source: data.word,
                        target: item.word,
                        type: "resolved",
                        rela:"相关性:" + item.frequency.toFixed(2)
                    });
                    if(item.relations.length != 0){
                        item.relations.map(function (rela) {
                            relations.push({
                                source: item.word,
                                target: rela.word,
                                type: "resolved",
                                rela:"相关性:" + rela.frequency.toFixed(2)
                            });
                        });
                    }
                });
                relateWord(relations);
                this.setState({seData,relations});
            }).catch((err) => console.log(err.toString()));
    }

    //二级关联
    Secondary(flag){
        let {seData} = this.state;
        let relations = [];
        if(flag == 1){
            seData.relations.map(function (item) {
                relations.push({
                    source: seData.word,
                    target: item.word,
                    type: "resolved",
                    rela:"相关性:" + item.frequency.toFixed(2)
                });
            });
        }else if(flag == 2){
            seData.relations.map(function (item) {
                if(item.relations.length != 0){
                    item.relations.map(function (rela) {
                        relations.push({
                            source: item.word,
                            target: rela.word,
                            type: "resolved",
                            rela:"相关性:" + rela.frequency.toFixed(2)
                        });
                    });
                }
            });
        }else {
            seData.relations.map(function (item) {
                relations.push({
                    source: seData.word,
                    target: item.word,
                    type: "resolved",
                    rela:"相关性:" + item.frequency.toFixed(2)
                });
                if(item.relations.length != 0){
                    item.relations.map(function (rela) {
                        relations.push({
                            source: item.word,
                            target: rela.word,
                            type: "resolved",
                            rela:"相关性:" + rela.frequency.toFixed(2)
                        });
                    });
                }
            });
        }

        relateWord(relations);
    }




    render() {
        let {relations,show} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'900px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/relatedWords'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in18.png"/><span className="classTitle">相关词</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px',height:'100px'}}>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                <Label>模型名称:</Label>
                                <Input placeholder="请输入模型名称" type="search" style={{width:'30%',display:'inline-block'}} getter={ this.getModel.bind(this) }/>
                                <Label>关键字:</Label>
                                <Input placeholder="请输入关键字" type="search" style={{width:'30%',display:'inline-block'}} getter={ this.getInput.bind(this) }/>
                                <Button type="info" style={{margin:'0 20px'}} onClick={this.search.bind(this)}>查找</Button>
                            </Col>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">相关词</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                    <Button type="info" style={{margin:'0 20px'}} onClick={this.Secondary.bind(this,0)}>还原</Button>
                                    <Button type="info" style={{margin:'0 20px'}} onClick={this.Secondary.bind(this,1)}>一级关联</Button>
                                    <Button type="info" style={{margin:'0 20px'}} onClick={this.Secondary.bind(this,2)}>二级关联</Button>
                                    {show ? (relations ? null : <Loading />) : null}
                                    <div id="chart"></div>
                                </Col>
                            </div>
                        </Row>
                    </Col>
                </Row>

                <Footer />
            </Page>
        );
    }

}
RelatedWords.UIPage = page;
export default RelatedWords;