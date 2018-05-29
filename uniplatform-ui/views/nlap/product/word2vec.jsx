import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Upload,
    Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: 'Word2Vec',
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

class Word2Vec extends Component {
    constructor(props){
        super(props);

        this.state = {
        }
    }

    componentDidMount() {
    }

    search(event){
        if (event.which==13){
            let keyWord = this.getValue();
            this.setState({keyWord}, () => this.handleValue());
        }
    }
    getInput(getter){
        this.getValue=getter.value;
    }

    handleValue() {
        this.setState({relations:undefined});
        const word = this.getValue();
        if(!word.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请输入关键字"/>);
            return;
        }
        let show = true;
        this.setState({show});
        let param = new FormData();
        param.append('word', word);
        fetch(Uniplatform.context.url + '/nlap/admin/word2Vec/word2Vectext', {
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                    this.setState({relations:[]});
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                    this.setState({relations:[]});
                }
                let seData = data;
                let relations = [];
                data.result.map(function (item) {
                    relations.push({
                        source: word,
                        target: item.name,
                        type: "resolved",
                        rela:"相关性:" + item.vate.toFixed(2)
                    });
                    if(item.children.length != 0){
                        item.children.map(function (rela) {
                            relations.push({
                                source: item.name,
                                target: rela.name,
                                type: "resolved",
                                rela:"相关性:" + rela.vate.toFixed(2)
                            });
                        });
                    }
                });
                relateWord(relations);
                this.setState({seData,word,relations});
            }).catch((err) => console.log(err.toString()));
    }

    Secondary(flag){
        let {seData,word} = this.state;
        let relations = [];
        if(flag == 1){
            seData.result.map(function (item) {
                relations.push({
                    source: word,
                    target: item.name,
                    type: "resolved",
                    rela:"相关性:" + item.vate.toFixed(2)
                });
            });
        }else if(flag == 2){
            seData.result.map(function (item) {
                if(item.children.length != 0){
                    item.children.map(function (rela) {
                        relations.push({
                            source: item.name,
                            target: rela.name,
                            type: "resolved",
                            rela:"相关性:" + rela.vate.toFixed(2)
                        });
                    });
                }
            });
        }else {
            seData.result.map(function (item) {
                relations.push({
                    source: word,
                    target: item.name,
                    type: "resolved",
                    rela:"相关性:" + item.vate.toFixed(2)
                });
                if(item.children.length != 0){
                    item.children.map(function (rela) {
                        relations.push({
                            source: item.name,
                            target: rela.name,
                            type: "resolved",
                            rela:"相关性:" + rela.vate.toFixed(2)
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
                            <ProductMenu url={'/nlap/product/word2vec'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in11.png"/><span className="classTitle">Word2Vec</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px',height:'100px'}}>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                <Label>关键字:</Label>
                                <Input placeholder="请输入关键字" type="search"
                                       onKeyPress={ this.search.bind(this)}
                                       getter={ this.getInput.bind(this) }
                                       style={{width:'30%',display:'inline-block'}}>
                                    <Input.Left icon="search"/>
                                </Input>
                                <Button type="info" style={{margin:'0 20px'}} onClick={this.handleValue.bind(this)}>搜索</Button>
                            </Col>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">Word2vec</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                    <Button type="info" style={{margin:'0 20px'}} onClick={this.Secondary.bind(this,0)}>还原</Button>
                                    <Button type="info" style={{margin:'0 20px'}} onClick={this.Secondary.bind(this,1)}>一级关联</Button>
                                    <Button type="info" style={{margin:'0 20px'}} onClick={this.Secondary.bind(this,2)}>二级关联</Button>
                                    {show ?
                                        (relations ? (relations.length==0? <span>无结果</span> : null) : <Loading />)
                                        : null
                                    }
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
Word2Vec.UIPage = page;
export default Word2Vec;