import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text} from 'epm-ui';
import { Footer,COMM_HeadBanner,NLPMenu,LabelManage,ModelManage,BusinessStrategyManage,ClassManage,
    CorpusManage,DataSet,DictionaryManage,StrategyManage,RuleManage,TaskManage} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: 'NLAP',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/dicManage.min.css',
        '../css/rules.min.css',
        '../css/corpusManage.min.css',
        '../css/dataSet.min.css',
        '../css/classManage.min.css',
        '../css/treechart.min.css'
    ]
};

class IndexApp extends Component {
    constructor(props){
        super(props);

        this.state = {
            index:1,
            size:10,
            total:0,
            userList : [],
            visible:false,
            updateVisible:false,
            crawlerData: [],
            siteData: [],
            dateData: [],
            taskData: [],
            unsucTaskData:[]
        }
        this.getHref=this.getHref.bind(this);
        this.menu = this.menu.bind(this);
    }

    componentDidMount() {
    }

    getHref(href){
        this.setState({href},()=>{this.menu()});
    }

    menu(){
        let {href} = this.state;
        let pageHtml = [];
        if(href == '/nlap/platform/labelManage'){
            pageHtml.push(<LabelManage key=""/>);
        }else if(href == '/nlap/platform/model'){
            pageHtml.push(<ModelManage key=""/>);
        }else if(href == '/nlap/platform/dictionary'){
            pageHtml.push(<DictionaryManage key=""/>);
        }else if(href == '/nlap/platform/rules'){
            pageHtml.push(<RuleManage key=""/>);
        }else if(href == '/nlap/platform/classManage'){
            pageHtml.push(<ClassManage key=""/>);
        }else if(href == '/nlap/platform/corpusManage'){
            pageHtml.push(<CorpusManage key=""/>);
        }else if(href == '/nlap/platform/dataSet'){
            pageHtml.push(<DataSet key=""/>);
        }else if(href == '/nlap/platform/funcStrategy'){
            pageHtml.push(<StrategyManage key=""/>);
        }else if(href == '/nlap/platform/busiStrategy'){
            pageHtml.push(<BusinessStrategyManage key=""/>);
        }else if(href == '/nlap/platform/task'){
            pageHtml.push(<TaskManage key=""/>);
        }
        this.setState({pageHtml});
    }

    render() {
        let {pageHtml} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{padding:'0',backgroundColor:'#fff',height:'1000px'}}>
                        <NLPMenu href={this.getHref}/>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        {pageHtml}
                    </Col>
                </Row>

                <Footer />
            </Page>
        );
    }

}
IndexApp.UIPage = page;
export default IndexApp;