import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Upload,
    Transfer,Dialog,Dropdown,Progress } from 'epm-ui';
/*import { Footer,COMM_HeadBanner,NLPMenu} from '../../../components/uniplatform-ui';*/

const contextUrl = '/uniplatform';

const page = {
    title: '词库管理',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/dicManage.min.css'
    ]
};

class DictionaryManage extends Component {
    constructor(props){
        super(props);

        this.state = {
            index:1,
            size:9,
            total:0,
            index_table:1,
            size_table:10,
            total_table:0,
            userList : [],
            visible:false,
            updateVisible:false,
            dictionaryData: [],
            dicTypeId: [],
            dicTypeNameData: [],
            dicNumberData: [],
            dicSingleData: [],
            selectedData: null,
            isDicDetail: false,
            inputHtml: [],
            addDicVisible: false,
            isWrite: true,
            addType: [],
            addSubType: [],
            addLabelType: [],
            dicData: [],
            wordMap: [],
            wordOrdinary: [],
            dicLabelVisible: false,
            leftList: [],
            rightList: [],
            unusedList: [],
            usedList: [],
            updateMapWordVisible: false,
            updateOrdWordVisible: false,
            updateSenWordVisible: false,
            updateDicVisible: false,
            uploadWordVisible: false,
            natureNameList: [],
            isUse: false,
            fieldType: [],
            addLabelVisible: false,
            addWordLabelVisible: false,
            labelsList: [],
            wordLabel: '',
            progressBool: false,
            progress_bool: false
        }
        this.getFetchDictionaries = this.getFetchDictionaries.bind(this);
        this.getFetchAllDic = this.getFetchAllDic.bind(this);
        this.handleSelect = this.handleSelect.bind( this );
        this.getWordInput = this.getWordInput.bind(this);
        this.getDicInput = this.getDicInput.bind(this);
        this.handleApprove = this.handleApprove.bind( this );
        this.afterSubmit = this.afterSubmit.bind(this);
        this.afterAddSubmit = this.afterAddSubmit.bind(this);
        this.formTirgger = this.formTirgger.bind( this );
        this.formFileTirgger = this.formFileTirgger.bind(this);
        this.beforeDicSubmit = this.beforeDicSubmit.bind(this);
        this.formUploadTirgger = this.formUploadTirgger.bind(this);
        this.formAddMapTirgger = this.formAddMapTirgger.bind(this);
        this.formAddOrdTirgger = this.formAddOrdTirgger.bind(this);
        this.afterAddDicSubmit = this.afterAddDicSubmit.bind(this);
        this.dictionaryList = this.dictionaryList.bind(this);
        this.beforeUploadSubmit = this.beforeUploadSubmit.bind(this);
        this.beforeWordSubmit = this.beforeWordSubmit.bind(this);
        this.beforeWordUpdateSubmit = this.beforeWordUpdateSubmit.bind(this);
        this.beforeMapWordSubmit = this.beforeMapWordSubmit.bind(this);
        this.formAddSenTirgger = this.formAddSenTirgger.bind(this);
        this.beforeSenWordSubmit = this.beforeSenWordSubmit.bind(this);
    }

    componentDidMount() {
        this.getFetchDictionaries();
        this.getFetchAllDic();
        //this.showAllDictionaries();
    }

    //获取各类词库统计信息
    getFetchDictionaries(){
        fetch(Uniplatform.context.url + '/nlap/admin/tag/init/init/label', {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let dictionaryData = [];
                let dicTypeNameData = [];
                let dicNumberData = [];
                let dicTypeId = [];
                let dicSubType = [];
                let dicLabel = [];
                let addLabelType;
                let subTypes = [];
                data.labels.map(function (item,index) {
                    dicTypeId.push(item.dicTypeId);
                    dicTypeNameData.push(item.dicTypeName);
                    dicNumberData.push(item.dicNumber);
                    subTypes.push(item.subTypes);

                    if(index == 0){
                        item.subTypes.map(function (sub) {
                            dicSubType.push({
                                subTypeName : sub.subType.disPlayName,
                                dicTypeId : sub.subType.dicTypeId,
                                format : sub.subType.format,
                                dicSubTypeId : sub.subType.id,
                            });
                        });
                        /*addLabelType = item.function.map(function (fun) {
                         let labelType = {value:fun.functionId,text:fun.function};
                         return labelType;
                         });*/
                    }
                    dictionaryData.push({
                        dicTypeName : item.dicTypeName,
                        dicTypeId:item.dicTypeId,
                        dicSubTypes: item.subTypes
                    });
                });
                this.setState({dictionaryData,dicTypeId,dicTypeNameData,dicNumberData,addLabelType,
                    dicSubType,dicLabel});
                this.getDicType('全部词库');
            }).catch((err) => console.log(err.toString()));
    }

    //获取所有词库
    getFetchAllDic(){
        let {dicType,subType,labelType,searchDic} = this.state;
        let param = new FormData();
        let {index,size,dictionaryData} = this.state;
        let dicTypeId_search = dicType ? dicType : '';
        let subTypeId_search = subType ? subType : '';
        let dicLabelId_search = labelType ? labelType : '';
        let keyWord_search = searchDic ? searchDic : '';
        param.append('pageIndex', index);
        param.append('pageSize', size);
        param.append('dicTypeId',dicTypeId_search);
        param.append('subTypeId',subTypeId_search);
        param.append('functionId',dicLabelId_search);
        param.append('keyWord',keyWord_search);
        fetch(Uniplatform.context.url + '/nlap/admin/dicMgmt/queryDic', {
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
                let dicData = [];
                let total = data.totalNumber;
                data.dics.map(function (dic) {
                    let labelNameList=[];
                    let labelIdList=[];
                    dic.function.map(function (func) {
                        labelNameList.push(func.function);
                        labelIdList.push(func.functionId);
                    });
                    dicData.push({
                        dicId : dic.dicId,
                        format : dic.format,
                        dicName : dic.dicName,
                        typeName : dic.typeName,
                        subTypeName : dic.subTypeName,
                        wordNum : dic.wordNum,
                        labelNameList : labelNameList,
                        labelIdList : labelIdList,
                        dicStatus : dic.dicStatus
                    });
                });
                this.setState({dicData,total,dicTypeId_search,subTypeId_search,dicLabelId_search,keyWord_search});
                this.dictionaryList();
            }).catch((err) => console.log(err.toString()));
    }

    //获取词库类型
    getDicType(dicType){
        let {dicTypeNameData,dictionaryData,dicTypeId,dicSubType,dicLabel,backLabelIndex} = this.state;
        let typeHtml = [], subtTypeHtml = [], labelHtml = [];
        for(let i=0; i<dicTypeNameData.length; i++){
            typeHtml.push(
                <a key={i} className="aType" onClick={this.clickDicType.bind(this,dicTypeNameData[i],dicTypeId[i],i)}>{dicTypeNameData[i]}</a>
            );
        }

        let dicSubTypeName = [];
        let dicSubTypeId = [];
        let dict_typeId = '';
        let dicLableName = [];
        let dicLableId = [];
        let dict_subTypeId = '';
        for(let i=0; i<dictionaryData.length; i++){
            if(dictionaryData[i].dicTypeName == dicType){
                dict_typeId = dictionaryData[i].dicTypeId;
                dictionaryData[i].dicSubTypes.map(function (sub) {
                    dicSubTypeName.push(sub.subType.disPlayName);
                    dicSubTypeId.push(sub.subType.id);
                    if(sub.subType.disPlayName == '全部'){
                        dict_subTypeId = sub.subType.id;
                        sub.function.map(function (func) {
                            dicLableName.push(func.displayName);
                            dicLableId.push(func.id);
                        });
                    }
                });
            }
        }

        for(let i=0; i<dicSubTypeName.length; i++){
            let backColor = '';
            if(i == 0){
                backColor = '#dcdcdc';
            }
            subtTypeHtml.push(
                <a key={i} className="subType" style={{backgroundColor:backColor}} onClick={this.getLabelBySubType.bind(this,dicSubTypeName[i],dicSubTypeId[i],dicType,i)}>{dicSubTypeName[i]}</a>
            );
        }

        for(let i=0; i<dicLableName.length; i++){
            let backColor = '';
            if(backLabelIndex == i){
                backColor = '#dcdcdc';
            }else if(i == 0){
                backColor = '#dcdcdc';
            }
            labelHtml.push(
                <a key={i} className="lType" style={{backgroundColor:backColor}} onClick={this.clickLabelType.bind(this,dict_typeId,dict_subTypeId,dicLableId[i],i)}>{dicLableName[i]}</a>
            );
        }

        this.setState({typeHtml,subtTypeHtml,labelHtml});
        this.showAllDictionaries();
    }

    //根据子类型获取对应标签
    getLabelBySubType(dicSubTypeName,dicSubTypeId,dicType,index){
        let {dictionaryData} = this.state;
        let labelHtml = [];
        let dicLableName = [];
        let dicLableId = [];
        let dict_typeId = '';
        for(let i=0; i<dictionaryData.length; i++){
            if(dictionaryData[i].dicTypeName == dicType){
                dict_typeId = dictionaryData[i].dicTypeId;
                dictionaryData[i].dicSubTypes.map(function (sub) {
                    if(dicSubTypeName == sub.subType.disPlayName){
                        sub.function.map(function (func) {
                            dicLableName.push(func.displayName);
                            dicLableId.push(func.id);
                        });
                    }
                });
            }
        }
        for(let i=0; i<dicLableName.length; i++){
            let backColor = undefined;
            if(i == 0){
                backColor = '#dcdcdc';
            }
            labelHtml.push(
                <a key={i} className="lType" style={{backgroundColor:backColor}} onClick={this.clickLabelType.bind(this,dict_typeId,dicSubTypeId,dicLableId[i],i)}>{dicLableName[i]}</a>
            );
        }

        //this.setState({labelHtml});
        this.setState({labelHtml,dicType:dict_typeId,subType:dicSubTypeId,labelType:undefined,searchDic:undefined}, ()=>{this.getFetchAllDic();this.showAllDictionaries();});
        //this.showAllDictionaries();
        // this.getDicType(dicTypeName);


        //点击子类型后背景色变化
        let sub_link = document.getElementsByClassName('subType');
        //let lable_link = document.getElementsByClassName('lType');
        for(let i=0;i<sub_link.length;i++){
            sub_link[i].style.backgroundColor = null;
        }
        /*for(let i=0;i<lable_link.length;i++){
         lable_link[i].style.backgroundColor = null;
         }
         lable_link[0].style.backgroundColor = '#dcdcdc';*/
        sub_link[index].style.backgroundColor = '#dcdcdc';

        this.setState({backDicSubTypeIndex:index,backLabelIndex:undefined,backDicTypeId : dict_typeId,backDicSubTypeName:dicSubTypeName,
            backDicSubTypeId:dicSubTypeId,index:1});
    }

    //词库&词详情显示切换div
    showAllDictionaries(){
        let {typeHtml,subtTypeHtml,labelHtml,wordMap,wordOrdinary,wordSentiment,addWord_dicId,index,size,total,
            index_table,total_table,size_table,isUse,dicTypeId_search,subTypeId_search,dicLabelId_search,
            keyWord_search,backDicSubTypeIndex,backLabelIndex,noData} = this.state;
        if(subtTypeHtml){
            let sub_link = document.getElementsByClassName('subType');
            let lable_link = document.getElementsByClassName('lType');
            if(sub_link.length){
                for(let i=0;i<sub_link.length;i++){
                    sub_link[i].style.backgroundColor = null;
                }
                for(let i=0;i<lable_link.length;i++){
                    lable_link[i].style.backgroundColor = null;
                }
                let index_sub = backDicSubTypeIndex ? backDicSubTypeIndex : 0;
                let index_lable = backLabelIndex ? backLabelIndex : 0;
                sub_link[index_sub].style.backgroundColor = '#dcdcdc';
                lable_link[index_lable].style.backgroundColor = '#dcdcdc';
            }
        }
        let dicHtml = [];
        if(this.state.isDicDetail){
            let dictionaryId = addWord_dicId;
            if(this.state.format == "map"){
                dicHtml.push(
                    <div key="">
                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                            <label>词条详情</label>
                            <Button type="default" size="medium" onClick={this.showDic.bind(this)} style={{float:'right',marginRight:'15px'}}>
                                <i className="fa fa-reply"></i> 返回
                            </Button>
                            {noData ?
                                <Button type="default" size="medium" style={{float:'right'}} disabled>
                                    <i className="fa fa-trash"></i> 批量删除
                                </Button>
                                :
                                <Button type="default" size="medium" onClick={this.deleteWords.bind(this,dictionaryId,this.state.selectedData,this.state.selectedWordStatus)} style={{float:'right'}} disabled={isUse}>
                                    <i className="fa fa-trash"></i> 批量删除
                                </Button>
                            }
                            <Button type="default" size="medium" style={{float:'right'}} onClick={this.uploadWordModal.bind(this,'map')} disabled={isUse}>
                                <i className="fa fa-upload"></i> 上传词
                            </Button>
                            <Button type="default" size="medium" onClick={this.showAddWord.bind(this,this.state.format,dictionaryId)} style={{float:'right'}} disabled={isUse}>
                                <i className="fa fa-plus-square-o"></i> 添加词
                            </Button>
                            <Col size={6} style={{position: 'relative',float:'right',zIndex: '1'}}>
                                <Input placeholder="请输入要查询的词" type="search"
                                       onKeyPress={ this.wordSearch.bind(this)}
                                       getter={ this.getWordInput }
                                       onChange={this.wordSearchAuto.bind(this)}>
                                    <Input.Left icon="search" />
                                </Input>
                            </Col>
                        </Col>
                        <div className="dicManage_content">
                            {noData ?
                                <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                    暂无数据
                                </div>
                                :
                                <Col>
                                    <Table  striped={true} multiLine={true} checkable headBolder style={{color:'#54698d'}}
                                            onCheck={ this.handleSelect } dataSource={wordMap}>
                                        <Column title="序号" scaleWidth = '5%' textAlign="center">
                                            { ( value, index ) => { return Number(`${Number(this.state.index_table-1)*Number(this.state.size_table)+Number(index)}`) + 1; } }
                                        </Column>
                                        <Column dataIndex="wordKey" title="键" scaleWidth='25%' textAlign="center"/>
                                        <Column dataIndex="wordValue" title="值" scaleWidth='25%' textAlign="center"/>
                                        <Column title="状态" scaleWidth='20%' textAlign="center">
                                            {(value)=>{
                                                let status;
                                                switch (value.wordStatus) {
                                                    case 0:
                                                        status = '已停用';
                                                        break;
                                                    default:
                                                        status = '已启用';
                                                        break;
                                                }
                                                return status;
                                            }}
                                        </Column>
                                        <Column title="操作" scaleWidth='25%' textAlign="center" >
                                            {( value )=>{
                                                return(
                                                    <div>
                                                        {value.wordStatus ?
                                                            <Button type="default" size="tiny" disabled>
                                                                编辑
                                                            </Button>
                                                            :
                                                            <Button type="default" size="tiny" onClick={this.updateMapWordModal.bind(this,value.dictionaryId,value.wordId)} disabled={isUse}>
                                                                编辑
                                                            </Button>
                                                        }
                                                        {value.wordStatus ?
                                                            <Button type="default" size="tiny" disabled>
                                                                删除
                                                            </Button>
                                                            :
                                                            <Button type="default" size="tiny" onClick={this.deleteWord.bind(this,value.dictionaryId,value.wordId)} disabled={isUse}>
                                                                删除
                                                            </Button>
                                                        }
                                                        {value.wordStatus ?
                                                            <Button type="default" size="tiny"
                                                                    onClick={this.stopWord.bind(this, value.dictionaryId, value.wordId)} disabled={isUse}>
                                                                停用
                                                            </Button>
                                                            :
                                                            <Button type="default" size="tiny"
                                                                    onClick={this.startWord.bind(this, value.dictionaryId, value.wordId)} disabled={isUse}>
                                                                启用
                                                            </Button>
                                                        }
                                                    </div>
                                                )
                                            }}
                                        </Column>
                                    </Table>
                                    <Divider/>
                                    <Pagination align='center' index={index_table} total={total_table} size={size_table} onChange={this.exChangeWordPagi.bind(this)}
                                                showDataSizePicker={true} dataSizePickerList={['10', '20', '50', '100']}/>
                                    <Divider/>
                                </Col>
                            }

                        </div>
                    </div>
                );
            }else if(this.state.format == "ordinary"){
                if(this.state.typeName == '领域词库'){
                    dicHtml.push(
                        <div key="">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                <label>词条详情</label>
                                <Button type="default" size="medium" onClick={this.showDic.bind(this)} style={{float:'right',marginRight:'15px'}}>
                                    <i className="fa fa-reply"></i> 返回
                                </Button>
                                {noData ?
                                    <Button type="default" size="medium" style={{float:'right'}} disabled>
                                        <i className="fa fa-trash"></i> 批量删除
                                    </Button>
                                    :
                                    <Button type="default" size="medium" onClick={this.deleteWords.bind(this,dictionaryId,this.state.selectedData,this.state.selectedWordStatus)} style={{float:'right'}} disabled={isUse}>
                                        <i className="fa fa-trash"></i> 批量删除
                                    </Button>
                                }
                                <Button type="default" size="medium" style={{float:'right'}} onClick={this.uploadWordModal.bind(this,'ordinary')} disabled={isUse}>
                                    <i className="fa fa-upload"></i> 上传词
                                </Button>
                                <Button type="default" size="medium" onClick={this.showAddWord.bind(this,this.state.format,dictionaryId)} style={{float:'right'}} disabled={isUse}>
                                    <i className="fa fa-plus-square-o"></i> 添加词
                                </Button>
                                <Button type="default" size="medium" onClick={this.showAddLabel.bind(this,dictionaryId)} style={{float:'right'}} disabled={isUse}>
                                    <i className="fa fa-clipboard"></i> 打标签
                                </Button>
                                <Col size={6} style={{position: 'relative',float:'right',zIndex: '1'}}>
                                    <Input placeholder="请输入要查询的词" type="search"
                                           onKeyPress={ this.wordSearch.bind(this)}
                                           getter={ this.getWordInput }
                                           onChange={this.wordSearchAuto.bind(this)}>
                                        <Input.Left icon="search" />
                                    </Input>
                                </Col>
                            </Col>
                            <div className="dicManage_content">
                                {noData ?
                                    <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                        暂无数据
                                    </div>
                                    :
                                    <Col>
                                        <Table  striped={true} multiLine={true} checkable headBolder style={{color:'#54698d'}}
                                                onCheck={ this.handleSelect } dataSource={wordOrdinary}>
                                            <Column title="序号" scaleWidth = '5%' textAlign="center">
                                                { ( value, index ) => { return Number(`${Number(this.state.index_table-1)*Number(this.state.size_table)+Number(index)}`) + 1; } }
                                            </Column>
                                            <Column dataIndex="word" title="词名称" scaleWidth='20%' textAlign="center"/>
                                            <Column dataIndex="wordLabel" title="词标签" scaleWidth='20%' textAlign="center"/>
                                            <Column title="词性" scaleWidth='15%' textAlign="center">
                                                { ( value ) => {
                                                    let result = this.getNature(value.nature);
                                                    return result;
                                                } }
                                            </Column>
                                            <Column dataIndex="frequency" title="词频" scaleWidth='10%' textAlign="center"/>
                                            <Column title="状态" scaleWidth='10%' textAlign="center" >
                                                {(value)=>{
                                                    let status;
                                                    switch (value.wordStatus) {
                                                        case 0 :
                                                            status = '已停用';
                                                            break;
                                                        default:
                                                            status = '已启用';
                                                            break;
                                                    }
                                                    return status;
                                                }}
                                            </Column>
                                            <Column title="操作" scaleWidth='20%' textAlign="center" >
                                                {( value )=>{
                                                    return(
                                                        <div>
                                                            <Button type="default" size="tiny" onClick={this.showAddWordLabel.bind(this,value.wordId)} disabled={isUse}>
                                                                打标签
                                                            </Button>
                                                            {value.wordStatus ?
                                                                <Button type="default" size="tiny" disabled>编辑</Button>
                                                                :
                                                                <Button type="default" size="tiny" onClick={this.updateOrdWordModal.bind(this,value.dictionaryId,value.wordId)} disabled={isUse}>
                                                                    编辑
                                                                </Button>
                                                            }
                                                            {value.wordStatus ?
                                                                <Button type="default" size="tiny" disabled>删除</Button>
                                                                :
                                                                <Button type="default" size="tiny" onClick={this.deleteWord.bind(this,value.dictionaryId,value.wordId)} disabled={isUse}>
                                                                    删除
                                                                </Button>
                                                            }

                                                            {value.wordStatus ?
                                                                <Button type="default" size="tiny"
                                                                        onClick={this.stopWord.bind(this, value.dictionaryId, value.wordId)} disabled={isUse}>
                                                                    停用
                                                                </Button>
                                                                :
                                                                <Button type="default" size="tiny"
                                                                        onClick={this.startWord.bind(this, value.dictionaryId, value.wordId)} disabled={isUse}>
                                                                    启用
                                                                </Button>
                                                            }
                                                        </div>
                                                    )
                                                }}
                                            </Column>
                                        </Table>
                                        <Divider/>
                                        <Pagination align='center' index={index_table} total={total_table} size={size_table} onChange={this.exChangeWordPagi.bind(this)}
                                                    showDataSizePicker={true} dataSizePickerList={['10', '20', '50', '100']}/>
                                        <Divider/>
                                    </Col>
                                }


                            </div>
                        </div>
                    );
                }else {
                    dicHtml.push(
                        <div key="">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                <label>词条详情</label>
                                <Button type="default" size="medium" onClick={this.showDic.bind(this)} style={{float:'right',marginRight:'15px'}}>
                                    <i className="fa fa-reply"></i> 返回
                                </Button>
                                {noData ?
                                    <Button type="default" size="medium" style={{float:'right'}} disabled>
                                        <i className="fa fa-trash"></i> 批量删除
                                    </Button>
                                    :
                                    <Button type="default" size="medium" onClick={this.deleteWords.bind(this,dictionaryId,this.state.selectedData,this.state.selectedWordStatus)} style={{float:'right'}} disabled={isUse}>
                                        <i className="fa fa-trash"></i> 批量删除
                                    </Button>
                                }
                                <Button type="default" size="medium" style={{float:'right'}} onClick={this.uploadWordModal.bind(this,'ordinary')} disabled={isUse}>
                                    <i className="fa fa-upload"></i> 上传词
                                </Button>
                                <Button type="default" size="medium" onClick={this.showAddWord.bind(this,this.state.format,dictionaryId)} style={{float:'right'}} disabled={isUse}>
                                    <i className="fa fa-plus-square-o"></i> 添加词
                                </Button>
                                <Col size={6} style={{position: 'relative',float:'right',zIndex: '1'}}>
                                    <Input placeholder="请输入要查询的词" type="search"
                                           onKeyPress={ this.wordSearch.bind(this)}
                                           getter={ this.getWordInput }
                                           onChange={this.wordSearchAuto.bind(this)}>
                                        <Input.Left icon="search" />
                                    </Input>
                                </Col>
                            </Col>
                            <div className="dicManage_content">
                                {noData ?
                                    <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                        暂无数据
                                    </div>
                                    :
                                    <Col>
                                        <Table  striped={true} multiLine={true} checkable headBolder style={{color:'#54698d'}}
                                                onCheck={ this.handleSelect } dataSource={wordOrdinary}>
                                            <Column title="序号" scaleWidth = '5%' textAlign="center">
                                                { ( value, index ) => { return Number(`${Number(this.state.index_table-1)*Number(this.state.size_table)+Number(index)}`) + 1; } }
                                            </Column>
                                            <Column dataIndex="word" title="词名称" scaleWidth='25%' textAlign="center"/>
                                            <Column title="词性" scaleWidth='20%' textAlign="center">
                                                { ( value ) => {
                                                    let result = this.getNature(value.nature);
                                                    return result;
                                                } }
                                            </Column>
                                            <Column dataIndex="frequency" title="词频" scaleWidth='15%' textAlign="center"/>
                                            <Column title="状态" scaleWidth='20%' textAlign="center" >
                                                {(value)=>{
                                                    let status;
                                                    switch (value.wordStatus) {
                                                        case 0 :
                                                            status = '已停用';
                                                            break;
                                                        default:
                                                            status = '已启用';
                                                            break;
                                                    }
                                                    return status;
                                                }}
                                            </Column>
                                            <Column title="操作" scaleWidth='15%' textAlign="center" >
                                                {( value )=>{
                                                    return(
                                                        <div>
                                                            {value.wordStatus ?
                                                                <Button type="default" size="tiny" disabled>
                                                                    编辑
                                                                </Button>
                                                                :
                                                                <Button type="default" size="tiny" onClick={this.updateOrdWordModal.bind(this,value.dictionaryId,value.wordId)} disabled={isUse}>
                                                                    编辑
                                                                </Button>
                                                            }
                                                            {value.wordStatus ?
                                                                <Button type="default" size="tiny" disabled>
                                                                    删除
                                                                </Button>
                                                                :
                                                                <Button type="default" size="tiny" onClick={this.deleteWord.bind(this,value.dictionaryId,value.wordId)} disabled={isUse}>
                                                                    删除
                                                                </Button>
                                                            }

                                                            {value.wordStatus ?
                                                                <Button type="default" size="tiny"
                                                                        onClick={this.stopWord.bind(this, value.dictionaryId, value.wordId)} disabled={isUse}>
                                                                    停用
                                                                </Button>
                                                                :
                                                                <Button type="default" size="tiny"
                                                                        onClick={this.startWord.bind(this, value.dictionaryId, value.wordId)} disabled={isUse}>
                                                                    启用
                                                                </Button>
                                                            }
                                                        </div>
                                                    )
                                                }}
                                            </Column>
                                        </Table>
                                        <Divider/>
                                        <Pagination align='center' index={index_table} total={total_table} size={size_table} onChange={this.exChangeWordPagi.bind(this)}
                                                    showDataSizePicker={true} dataSizePickerList={['10', '20', '50', '100']}/>
                                        <Divider/>
                                    </Col>
                                }


                            </div>
                        </div>
                    );
                }

            }else {
                dicHtml.push(
                    <div key="">
                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                            <label>词条详情</label>
                            <Button type="default" size="medium" onClick={this.showDic.bind(this)} style={{float:'right',marginRight:'15px'}}>
                                <i className="fa fa-reply"></i> 返回
                            </Button>
                            {noData ?
                                <Button type="default" size="medium" style={{float:'right'}} disabled>
                                    <i className="fa fa-trash"></i> 批量删除
                                </Button>
                                :
                                <Button type="default" size="medium" onClick={this.deleteWords.bind(this,dictionaryId,this.state.selectedData,this.state.selectedWordStatus)} style={{float:'right'}} disabled={isUse}>
                                    <i className="fa fa-trash"></i> 批量删除
                                </Button>
                            }
                            <Button type="default" size="medium" style={{float:'right'}} onClick={this.uploadWordModal.bind(this,'sentiment')} disabled={isUse}>
                                <i className="fa fa-upload"></i> 上传词
                            </Button>
                            <Button type="default" size="medium" onClick={this.showAddWord.bind(this,this.state.format,dictionaryId)} style={{float:'right'}} disabled={isUse}>
                                <i className="fa fa-plus-square-o"></i> 添加词
                            </Button>
                            <Col size={6} style={{position: 'relative',float:'right',zIndex: '1'}}>
                                <Input placeholder="请输入要查询的词" type="search"
                                       onKeyPress={ this.wordSearch.bind(this)}
                                       getter={ this.getWordInput }
                                       onChange={this.wordSearchAuto.bind(this)}>
                                    <Input.Left icon="search" />
                                </Input>
                            </Col>
                        </Col>
                        <div className="dicManage_content">
                            {noData ?
                                <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                    暂无数据
                                </div>
                                :
                                <Col>
                                    <Table  striped={true} multiLine={true} checkable headBolder style={{color:'#54698d'}}
                                            onCheck={ this.handleSelect } dataSource={wordSentiment}>
                                        <Column title="序号" scaleWidth = '5%' textAlign="center">
                                            { ( value, index ) => { return Number(`${Number(this.state.index_table-1)*Number(this.state.size_table)+Number(index)}`) + 1; } }
                                        </Column>
                                        <Column dataIndex="word" title="词名称" scaleWidth='25%' textAlign="center"/>
                                        <Column title="词性" scaleWidth='20%' textAlign="center">
                                            { ( value ) => {
                                                let result = this.getNature(value.nature);
                                                return result;
                                            } }
                                        </Column>
                                        <Column dataIndex="grade" title="评分" scaleWidth='15%' textAlign="center"/>
                                        <Column title="状态" scaleWidth='20%' textAlign="center" >
                                            {(value)=>{
                                                let status;
                                                switch (value.wordStatus) {
                                                    case 0 :
                                                        status = '已停用';
                                                        break;
                                                    default:
                                                        status = '已启用';
                                                        break;
                                                }
                                                return status;
                                            }}
                                        </Column>
                                        <Column title="操作" scaleWidth='15%' textAlign="center" >
                                            {( value )=>{
                                                return(
                                                    <div>
                                                        {value.wordStatus ?
                                                            <Button type="default" size="tiny" disabled>
                                                                编辑
                                                            </Button>
                                                            :
                                                            <Button type="default" size="tiny" onClick={this.updateSenWordModal.bind(this,value.dictionaryId,value.wordId)} disabled={isUse}>
                                                                编辑
                                                            </Button>
                                                        }
                                                        {value.wordStatus ?
                                                            <Button type="default" size="tiny" disabled>
                                                                删除
                                                            </Button>
                                                            :
                                                            <Button type="default" size="tiny" onClick={this.deleteWord.bind(this,value.dictionaryId,value.wordId)} disabled={isUse}>
                                                                删除
                                                            </Button>
                                                        }
                                                        {value.wordStatus ?
                                                            <Button type="default" size="tiny"
                                                                    onClick={this.stopWord.bind(this, value.dictionaryId, value.wordId)} disabled={isUse}>
                                                                停用
                                                            </Button>
                                                            :
                                                            <Button type="default" size="tiny"
                                                                    onClick={this.startWord.bind(this, value.dictionaryId, value.wordId)} disabled={isUse}>
                                                                启用
                                                            </Button>
                                                        }
                                                    </div>
                                                )
                                            }}
                                        </Column>
                                    </Table>
                                    <Divider/>
                                    <Pagination align='center' index={index_table} total={total_table} size={size_table} onChange={this.exChangeWordPagi.bind(this)}
                                                showDataSizePicker={true} dataSizePickerList={['10', '20', '50', '100']}/>
                                    <Divider/>
                                </Col>
                            }


                        </div>
                    </div>
                );
            }
        }else {
            dicHtml.push(
                <div key="">
                    <Col size={{ normal: 18, small: 24, medium: 18, large: 18 }} className="filter">
                        {/*<span className="filter_span" style={{display:'none'}}><label className="filterLabel">类&nbsp;型：</label>{typeHtml}</span><br/>*/}
                        <span className="filter_span"><label className="filterLabel">类型：</label>
                            <div style={{display:'inline-block',width:'90%'}}>{subtTypeHtml}</div>
                        </span>
                        <br/>
                        <span className="filter_span"><label className="filterLabel" style={{float:'left'}}>标签：</label>
                            <div style={{display:'inline-block',width:'90%'}}>{labelHtml}</div>
                        </span>
                    </Col>
                    <div className="dicManage_content">
                        <Row>
                            <Col size={{ normal: 5, small: 24, medium: 5, large: 5 }} style={{position: 'relative',float:'right',zIndex: '1',marginTop:'20px'}}>
                                <Input placeholder="请输入要查询的关键字" type="search"
                                       onKeyPress={ this.dicSearch.bind(this)}
                                       getter={ this.getDicInput }
                                       trigger={ this.formSearchTirgger.bind(this) }
                                       onChange={this.wordSearchDicAuto.bind(this)}>
                                    <Input.Left icon="search"/>
                                </Input>
                            </Col>
                        </Row>
                        <Row>
                            { this.dictionaryList() }
                        </Row>
                        <Row>
                            <Pagination align='center' index={index} total={total} size={size}
                                        onChange={this.exChangeDicPagi.bind(this,dicTypeId_search,subTypeId_search,dicLabelId_search,keyWord_search)} />
                        </Row>
                        <Divider />
                    </div>
                </div>
            );

        }
        return dicHtml;
    }

    //词列表词性 中文显示
    getNature(data){
        let result = '';
        let { natureName } = this.state;
        Object.keys(natureName).map(function (item) {
            if(item == data){
                result = natureName[item];
            }
        });
        return result;
    }

    //词库分页
    exChangeDicPagi(dicTypeId_search,subTypeId_search,dicLabelId_search,keyWord_search,index,size){
        this.setState({loc:undefined});
        this.setState({index, size,dicType:dicTypeId_search,subType:subTypeId_search,labelType:dicLabelId_search,searchDic:keyWord_search},
            () => this.getFetchAllDic());
    }

    //词分页
    exChangeWordPagi(index_table, size_table){
        this.setState({index_table, size_table}, () => this.getFetchWordList(''));
    }

    //展示所有词库
    dictionaryList(){
        let {inputHtml,dicData,loc,isUse} = this.state;
        let tbodyHtml = [];
        let dicName = '';
        let typeName = '';
        let subTypeName = '';
        let wordNum = 0;
        let format = '';
        let dicId = '';
        let labelNameList = [];
        let dicStatus = 0;
        if(dicData){
            for(let i = 0;i<dicData.length; i++) {
                dicName = dicData[i].dicName;
                typeName = dicData[i].typeName;
                subTypeName = dicData[i].subTypeName;
                wordNum = dicData[i].wordNum;
                format = dicData[i].format;
                labelNameList = dicData[i].labelNameList;
                dicId = dicData[i].dicId;
                dicStatus = dicData[i].dicStatus;
                if(dicStatus == 2){
                    isUse = true;
                }else {
                    isUse = false;
                }

                let fontColor;
                switch (typeName) {
                    case '全部词库' :
                        fontColor = '#da8b5c';
                        break;
                    case '普通词库' :
                        fontColor = '#84c7e5';
                        break
                    case '功能词库' :
                        fontColor = '#98d37b';
                        break;
                    case '领域词库' :
                        fontColor = '#ff838d';
                        break;
                    default:
                        fontColor = '#0e1d39';
                        break;
                }

                let formatDisplay;
                switch (format) {
                    case 'ordinary' :
                        formatDisplay = '普通词';
                        break;
                    case 'map' :
                        formatDisplay = '键值对';
                        break
                    default:
                        formatDisplay = '情感词';
                        break;
                }

                let labodyHtml = [];
                let overLabel = [];
                if(labelNameList.length>0){
                    let colorList = ['#ff634d','#6fccf5','#98d37b','#ff634d'];
                    if(labelNameList.length<5){
                        for(let j=0;j<labelNameList.length;j++){
                            labodyHtml.push(
                                <Col size={{ normal: 12, small: 12, medium: 12, large: 12 }} style={{padding:'0'}} key={j}>
                                <span style={{backgroundColor:colorList[j],margin:'2px',padding:'2px',whiteSpace:'nowrap'}}>
                                    {labelNameList[j]}&nbsp;
                                </span>
                                </Col>
                            );
                        }
                    }
                    if(labelNameList.length>4){
                        for(let j=0;j<4;j++){
                            labodyHtml.push(
                                <Col size={{ normal: 12, small: 12, medium: 12, large: 12 }} style={{padding:'0'}} key={j}>
                                <span style={{backgroundColor:colorList[j],margin:'2px',padding:'2px',whiteSpace:'nowrap'}}>
                                    {labelNameList[j]}&nbsp;
                                </span>
                                </Col>
                            );
                        }
                        for(let j=0;j<labelNameList.length;j++){
                            overLabel.push(
                                <div style={{color:'#fff',backgroundColor:'#5a5a5a'}} key={j}>
                                    {labelNameList[j]}
                                </div>
                            );
                        }
                    }
                }

                tbodyHtml.push(
                    <Col style={{width:'20%'}} key={i}>
                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col" style={{background:fontColor}}>
                            <div className = "dicManage_title" >
                                {isUse ?
                                    (
                                        loc == i ?
                                            (inputHtml.length == 0 ?
                                                <span className = "dicManage_span" style={{height:'35px',textAlign:'center',textOverflow:'ellipsis',
                                                    overflow:'hidden',whiteSpace:'nowrap'}}>
                                                        <Dropdown position={'right'} key="">
                                                            <Dropdown.Trigger action="hover">
                                                                    <span>{dicName}</span>
                                                                </Dropdown.Trigger>
                                                                <Dropdown.Content>
                                                                    <div style={{color:'#fff',backgroundColor:'#5a5a5a'}}>
                                                                        {dicName}
                                                                    </div>
                                                                    {overLabel}
                                                                </Dropdown.Content>
                                                        </Dropdown>
                                                  </span>
                                                : inputHtml)
                                            :
                                            <span className = "dicManage_span" style={{height:'35px',textAlign:'center',backgroundColor:fontColor,
                                                textOverflow:'ellipsis',overflow:'hidden',whiteSpace:'nowrap'}}>
                                                    <Dropdown position={'right'} key="">
                                                        <Dropdown.Trigger action="hover">
                                                                    <span>{dicName}</span>
                                                                </Dropdown.Trigger>
                                                                <Dropdown.Content>
                                                                    <div style={{color:'#fff',backgroundColor:'#5a5a5a'}}>
                                                                        {dicName}
                                                                    </div>
                                                                    {overLabel}
                                                                </Dropdown.Content>
                                                    </Dropdown>
                                        </span>
                                    ):
                                    (
                                        loc == i ?
                                            (inputHtml.length == 0 ?
                                                <span className = "dicManage_span" style={{height:'35px',textAlign:'center',textOverflow:'ellipsis',
                                                    overflow:'hidden',whiteSpace:'nowrap'}}
                                                      onDoubleClick={this.updateDicName.bind(this,i,dicId,dicName)}>

                                                        <Dropdown position={'right'} key="">
                                                            <Dropdown.Trigger action="hover">
                                                                    <span>{dicName}</span>
                                                                </Dropdown.Trigger>
                                                                <Dropdown.Content>
                                                                    <div style={{color:'#fff',backgroundColor:'#5a5a5a'}}>
                                                                        {dicName}
                                                                    </div>
                                                                    {overLabel}
                                                                </Dropdown.Content>
                                                        </Dropdown>

                                                  </span>
                                                : inputHtml)
                                            :
                                            <span className = "dicManage_span" style={{height:'35px',textAlign:'center',backgroundColor:fontColor,
                                                textOverflow:'ellipsis',overflow:'hidden',whiteSpace:'nowrap'}}
                                                  onDoubleClick={this.updateDicName.bind(this,i,dicId,dicName)}>

                                                    <Dropdown position={'right'} key="">
                                                        <Dropdown.Trigger action="hover">
                                                                    <span>{dicName}</span>
                                                                </Dropdown.Trigger>
                                                                <Dropdown.Content>
                                                                    <div style={{color:'#fff',backgroundColor:'#5a5a5a'}}>
                                                                        {dicName}
                                                                    </div>
                                                                    {overLabel}
                                                                </Dropdown.Content>
                                                    </Dropdown>
                                        </span>
                                    )
                                }

                            </div>
                        </Col>
                        <div className="dicManage_content">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="cent" style={{height:'156px',zIndex:'1000'}}>
                                {/*<p style={{display:'none'}}><span style={{fontSize:'14px',width:'50%',float:'left',textAlign:'right'}}>类型：</span><span style={{fontSize:'16px',color:'#b67561',float:'left',textAlign:'left'}}>{typeName}</span></p>
                                 <br/>*/}
                                <p><span style={{fontSize:'14px',width:'40%',float:'left',textAlign:'right'}}>类型：</span><span style={{fontSize:'16px',width:'40%',color:'#b67561',float:'left',textAlign:'left'}}>{subTypeName ? subTypeName : ''}</span></p>
                                <p><span style={{fontSize:'14px',width:'40%',float:'left',textAlign:'right'}}>词量：</span><span style={{fontSize:'16px',width:'40%',color:'#b67561',float:'left',textAlign:'left'}}>{wordNum ? wordNum : 0}</span></p>
                                <p><span style={{fontSize:'14px',width:'40%',float:'left',textAlign:'right'}}>格式：</span><span style={{fontSize:'16px',width:'40%',color:'#b67561',float:'left',textAlign:'left'}}>{formatDisplay}</span></p>
                                <p><span style={{fontSize:'14px',width:'40%',float:'left',textAlign:'right'}}>状态：</span><span style={{fontSize:'16px',width:'40%',color:'#b67561',float:'left',textAlign:'left'}}>{dicStatus == 2 ? '启用':'未启用'}</span></p>
                                <div><span style={{fontSize:'14px',color:'#fff',float:'left',textAlign:'left'}}>{labodyHtml}</span></div>
                                <div className="layer"></div>
                                <div className="layer_but">
                                    {isUse == false? <a style={{backgroundColor: 'transparent', margin: '2%'}}
                                                        onClick={this.deleteDic.bind(this, dicId) }>
                                        <img src="../image/del.png"/></a>
                                        :
                                        <a style={{backgroundColor: 'transparent', margin: '2%',opacity:'0.5'}}>
                                            <img src="../image/del.png"/></a>
                                    }

                                    <a style={{backgroundColor:'transparent',margin:'2%'}} href={`/uniplatform/nlap/admin/dic/download/dic/download?dicId=${dicId}`}>
                                        <img src="../image/down.png"/>
                                    </a>

                                    {isUse == false? <a style={{backgroundColor:'transparent',margin:'2%'}} onClick={this.updateDicModal.bind(this,dicId)}>
                                        <img src="../image/edit.png" />
                                    </a>
                                        :
                                        <a style={{backgroundColor:'transparent',margin:'2%',opacity:'0.5'}}>
                                            <img src="../image/edit.png" />
                                        </a>
                                    }

                                    <a style={{backgroundColor:'transparent',margin:'2%'}} onClick={this.enabledModal.bind(this,dicId) }>
                                        <img src="../image/site.png" />
                                    </a>
                                    <Divider />
                                    <Button type="info" block style={{backgroundColor:'#6fccf5',color:'white'}} onClick={this.showDicDetail.bind(this,dicId,dicName,isUse,typeName)}>
                                        详情
                                    </Button>
                                </div>
                            </Col>
                            {/*<Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{height:'24px',fontSize:'12px',overflow:'hidden'}}>
                             {labodyHtml}
                             </Col>*/}
                        </div>
                    </Col>

                );
            }
            tbodyHtml.push(
                <Col style={{width:'20%'}} key="">
                    <div style={{height:'200px',border:'dashed 1px #d8dde8',borderRadius:'20px',textAlign:'center'}}>
                        <a type="default" size="tiny"
                           style={{float:'ri',margin: '10px auto', position: 'relative', zIndex: 1}}
                           onClick={this.showAddDicModal.bind(this)}
                        >
                            <Icon icon="plus" className="plus fa-5x" style={{margin:'28% 0px',height:'74px',width:'74px',color:'#d8dde8'}}/>
                        </a>
                    </div>
                </Col>
            );
        }else {
            popup(<Snackbar message="暂无数据"/>);
        }
        return tbodyHtml;
    }

    //查看词库详情
    showDicDetail(dicId,dicName,isUse,typeName){
        this.setState({dicId}, () => this.getFetchWordList(''));
        this.setState({dicName_detail : dicName,isUse,typeName});
        /*this.setState({ isDicDetail: true });
         this.showAllDictionaries();*/
    }
    //获取词列表（查看单个词库详情）
    getFetchWordList(searchWord){
        let param = new FormData();
        let {index_table,size_table,dicId} = this.state;
        let addWord_dicId = dicId;
        let search_word = searchWord ? searchWord : '';    //搜索词-关键词
        param.append('pageIndex', index_table);
        param.append('pageSize', size_table);
        param.append('dicId',dicId);
        param.append('searchWord',search_word);

        fetch(Uniplatform.context.url + '/nlap/admin/dicInfo/list', {
            credentials: 'same-origin',
            method: 'POST',
            async: false,
            body: param
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let noData = false;
                if(data.dicInfo.length == 0){
                    noData = true;
                }
                let fieldSubType = data.fieldType;
                let wordMap = [];
                let wordOrdinary = [];
                let wordSentiment = [];
                let format = data.format;
                let natureName = data.natureName;
                if(format == "map"){
                    data.dicInfo.map(function (item) {
                        wordMap.push({
                            dictionaryId : item.dictionaryId,
                            wordId : item.id,
                            wordKey : item.wordKey,
                            wordStatus : item.wordStatus,
                            wordValue : item.wordValue
                        });
                    });
                }else if(format == "ordinary"){
                    data.dicInfo.map(function (item) {
                        wordOrdinary.push({
                            dictionaryId : dicId,
                            wordId : item.id,
                            word : item.word,
                            wordLabel : item.label,
                            nature: item.nature,
                            frequency: item.frequency,
                            wordStatus : item.wordStatus
                        });
                    });
                }else {
                    data.dicInfo.map(function (item) {
                        wordSentiment.push({
                            dictionaryId : item.dictionaryId,
                            grade : item.grade,
                            wordId : item.id,
                            word : item.word,
                            nature: item.nature,
                            wordStatus : item.wordStatus
                        });
                    });
                }
                let total_table = data.totalWordNumber;
                this.setState({format,wordMap,wordOrdinary,wordSentiment,total_table,addWord_dicId,natureName,fieldSubType});
                this.setState({ isDicDetail: true, noData });
                this.showAllDictionaries();
                this.getNatureList();
            }).catch((err) => console.log(err.toString()));
    }
    //词库详情返回到全部词库页
    showDic(){
        this.setState({ isDicDetail: false, dicName_detail: undefined, index_table:1 },
            ()=>this.showAllDictionaries());
        this.getFetchAllDic();
    }

    handleSelect( data ) {
        this.setState( { selectedData: data.map( ( key ) => key.wordId ).join( ',' ),
            selectedWordStatus: data.map( ( key ) => key.wordStatus )} );
    }

    //添加词模态框
    showAddWord(format,dictionaryId){
        if(format == 'map'){
            this.resetAddMap();
            this.setState({ addMapWordVisible : true, dictionaryId:dictionaryId });
        }else if(format == 'ordinary'){
            //this.getNatureList();
            this.getFetchLablesList();
            this.resetAddOrd();
            this.formReset();
            this.setState({ addOrdWordVisible : true, dictionaryId:dictionaryId });
        }else {
            //this.getNatureList();
            this.resetAddSen();
            this.setState({ addSenWordVisible : true, dictionaryId:dictionaryId });
        }
    }
    //获取词属性列表
    getNatureList(){
        let { natureName } = this.state;
        let natureNameList = [];
        Object.keys(natureName).map(function (item) {
            natureNameList.push({
                value : item,
                text : natureName[item]
            });
        });
        this.setState({natureNameList});
    }
    closeAddWord(){
        this.setState({ addMapWordVisible : false });
        this.setState({ addOrdWordVisible : false });
        this.setState({ addSenWordVisible : false });
    }

    //根据词名称查询词
    wordSearch( event ) {
        if (event.which==13){
            let searchWord = this.getWordValue();
            this.getFetchWordList(searchWord);
        }
    }
    getWordInput(getter){
        this.getWordValue=getter.value;
    }
    wordSearchAuto(data){
        if(data == ''){
            this.getFetchWordList('');
        }
    }

    //根据词库名称查询词库
    dicSearch(event){
        if (event.which==13){
            let searchDic = this.getDicValue();
            this.setState({searchDic}, () => this.getFetchAllDic());
        }
    }
    getDicInput(getter){
        this.getDicValue=getter.value;
    }
    wordSearchDicAuto(data){
        if(data == ''){
            this.setState({ searchDic : '' }, () => this.getFetchAllDic());
        }
    }
    /*getFetchDicList(){
        let param = new FormData();
        let {size,index,dictionaryData,searchDic} = this.state;
        param.append('pageIndex', index);
        param.append('pageSize', size);
        param.append('name',searchDic);
        fetch(Uniplatform.context.url + '/nlap/admin/dicMgmt/dict/search', {
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res) => res.json())
            .then((data) => {
                if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let dicData = [];
                let total = data.searchNumber;
                data.searchDictionary.map(function (dic) {
                    let labelNameList=[];
                    let labelIdList=[];
                    dic.function.map(function (func) {
                        labelNameList.push(func.function);
                        labelIdList.push(func.functionId);
                    });
                    dicData.push({
                        dicId : dic.dicId,
                        format : dic.format,
                        dicName : dic.dicName,
                        subTypeName : dic.subTypeName,
                        wordNum : dic.wordNum,
                        dicStatus : dic.dicStatus,
                        typeName : dic.typeName,
                        labelNameList : labelNameList,
                        labelIdList : labelIdList
                    });
                });
                this.setState({dicData,total});
                this.dictionaryList();
                //this.setState({dicData, total,loc:null}, () => this.showAllDictionaries());
                //this.showAllDictionaries();
            }).catch((err) => console.log(err.toString()));
    }*/


    //添加词库模态框
    showAddDicModal(){
        this.reset();
        this.getFetchTypeList();
        this.setState({ addDicVisible : true });
    }
    closeAddDicModal(){
        this.resetFile();
        this.setState({ addDicVisible : false });
    }

    //编辑词库模态框
    updateDicModal(dicId){
        this.resetUpdateDic();
        this.resetEdit();
        this.getSingleDic(dicId);
        this.setState({ updateDicVisible : true });
    }
    //关闭编辑词库模态框
    closeUpdateDic(){
        this.setState({ updateDicVisible : false });
    }
    //编辑词库回显信息
    getSingleDic(dicId){
        let { dicData } = this.state;
        let dicName,dictId;
        let unusedList = [],usedList = [];
        for(let i = 0;i<dicData.length; i++) {
            if(dicData[i].dicId == dicId){
                dictId = dicData[i].dicId;
                dicName = dicData[i].dicName;
            }
        }
        fetch(Uniplatform.context.url + '/nlap/admin/dic/edit/tagginfo?dicId='+dicId, {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.status == 200){
                    data.beenFunction.map(function (item) {
                        usedList.push({
                            text: item.displayName,
                            key: item.id
                        });
                    });
                    data.functionAll.map(function (item) {
                        unusedList.push({
                            text: item.displayName,
                            key: item.id
                        });
                    });
                }
                if(data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                if(data.code == 2){
                    unusedList = [];
                    usedList = [];
                    popup(<Snackbar message={data.message}/>);
                }
                this.setState({dicName,dictId,unusedList,usedList});
            }).catch((err) => console.log(err.toString()));

        /*let { dicData,addLabelType } = this.state;
         let dicName,labelNameList,labelIdList,dictId;
         let unusedList = [];
         let usedList = [];
         for(let i = 0;i<dicData.length; i++) {
         if(dicData[i].dicId == dicId){
         dictId = dicData[i].dicId;
         dicName = dicData[i].dicName;
         labelNameList = dicData[i].labelNameList;
         labelIdList = dicData[i].labelIdList;
         }
         }
         console.log(labelNameList)
         for(let i=0; i<labelNameList.length; i++){
         usedList.push({
         text: labelNameList[i],
         key: labelIdList[i]
         });
         }
         for(let i=0; i<addLabelType.length; i++){
         let flag = false;
         if(labelIdList.length != 0){
         for(let j=0;j<labelIdList.length;j++){
         if(labelIdList[j] != addLabelType[i].value){
         flag = true;
         }else {
         flag = false;
         break;
         }
         }
         if(flag){
         unusedList.push({
         text: addLabelType[i].text,
         key: addLabelType[i].value
         });
         }
         }else {
         unusedList.push({
         text: addLabelType[i].text,
         key: addLabelType[i].value
         });
         }
         }
         console.log(usedList)
         this.setState({dicName,dictId,unusedList,usedList});*/
    }

    //词库启用标签模态框
    enabledModal(dicId){
        this.resetEnabled();
        this.enabled(dicId);
        this.setState({ dicLabelVisible : true });
    }
    //初始化启用标签和停用标签
    enabled(dicId){
        fetch(Uniplatform.context.url + '/nlap/admin/tag/init/label/dicLabel?dicId='+dicId, {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let disabledFunction = data.disabledFunction;
                let enabledFunction = data.enabledFunction;
                let leftList = [];
                let rightList = [];
                for(let i=0; i<disabledFunction.length; i++) {
                    let temp = {
                        text:disabledFunction[i].name,
                        key:disabledFunction[i].functionId
                    };
                    /*if ( i % 3 === 0 ) {
                     temp.disabled = true;
                     }*/
                    leftList.push(temp);
                }
                for(let i=0; i<enabledFunction.length; i++) {
                    rightList.push({
                        text: enabledFunction[i].name,
                        key: enabledFunction[i].functionId
                    });
                }

                this.setState({leftList,rightList,dicId});
            }).catch((err) => console.log(err.toString()));
    }
    //获取用户启用的标签
    handleListChange(leftList,rightList) {
        let selectLabelId = '';
        for(let i=0;i<rightList.length;i++){
            if(i == rightList.length-1){
                selectLabelId += rightList[i].key;
            }else {
                selectLabelId += rightList[i].key + ',';
            }
        }
        this.setState({selectLabelId});
    }

    //获取用户新加的标签
    ListChange(leftList,rightList){
        let newLabelId = '';
        for(let i=0;i<rightList.length;i++){
            if(i == rightList.length-1){
                newLabelId += rightList[i].key;
            }else {
                newLabelId += rightList[i].key + ',';
            }
        }
        this.setState({newLabelId});
    }
    //启用标签
    /*enabledLabel(selectLabelId,dicId){
     let param = new FormData();
     param.append('dicId', dicId);
     param.append('selectFunctionsId', selectLabelId);
     fetch(Uniplatform.context.url + '/nlap/admin/tag/init/label/dicEnable', {
     credentials: 'same-origin',
     method: 'POST',
     body: param
     })
     .then((res) => res.json())
     .then((data) => {
     popup(<Snackbar message={data.msg}/>);
     }).catch((err) => console.log(err.toString()));
     }*/

    //启用标签提交后
    afterEnabledSubmit(data){
        if(data.status == 200){
            this.getFetchAllDic();
            this.setState({ dicLabelVisible : false });
        }
        popup(<Snackbar message={data.msg}/>);
    }
    //关闭启用标签模态框
    closeEnabledModal(){
        this.setState({ dicLabelVisible : false });
    }

    //添加词库时获取词库类型及子类型
    getFetchTypeList(){
        fetch(Uniplatform.context.url + '/nlap/admin/dicMgmt/dict/info', {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let typeAddSubType = data.typeAddSubTypes;
                let addType = typeAddSubType.map(function (types) {
                    let at = {value:types.type.id,text:types.type.displayName};
                    return at;
                });
                this.setState({addType,typeAddSubType});
            }).catch((err) => console.log(err.toString()));
    }
    //添加词库时改变词库类型子类型随之改变
    dicTypeChange(id){
        this.resetDicType();
        let {typeAddSubType,isWrite,addType} = this.state;
        let isField = false;
        addType.map(function (item) {
            if((item.value == id) && (item.text == '领域词库')){
                isField = true;
            }
        });
        isWrite = false;
        let addSubType;
        typeAddSubType.map(function (item) {
            if(id == item.type.id){
                addSubType = item.subTypes.map(function (sub) {
                    let sub_type = { value : sub.subType.id, text : sub.subType.disPlayName };
                    return sub_type;
                });
            }
        });
        this.setState({addSubType,isWrite,add_typeId:id,isField});
    }
    //添加词库时改变词库子类型标签随之改变
    dicSubTypeChange(id){
        this.resetFieldType();
        let {typeAddSubType,add_typeId} = this.state;
        let addLabelType;
        let addFormat = '';
        typeAddSubType.map(function (item) {
            if(add_typeId == item.type.id){
                item.subTypes.map(function (sub) {
                    if(sub.subType.id == id){
                        addFormat = sub.subType.format;
                        addLabelType = sub.function.map(function (fun) {
                            let lla = { value : fun.id, text : fun.displayName };
                            return lla;
                        });
                    }
                });
            }
        });
        this.setState({addLabelType, addFormat});
        this.getFetchFieldType(id);
    }

    //编辑词（map）模态框
    updateMapWordModal(dictionaryId,wordId){
        this.getSingleMapWord(dictionaryId,wordId);
        this.setState({updateMapWordVisible : true});
    }
    closeUpdateMapWord(){
        this.setState({updateMapWordVisible : false});
    }
    //编辑词（ordinary）模态框
    updateOrdWordModal(dictionaryId,wordId){
        this.getSingleOrdWord(dictionaryId,wordId);
        this.getFetchLablesList();
        this.setState({updateOrdWordVisible : true});
    }
    closeUpdateOrdWord(){
        this.setState({updateOrdWordVisible : false});
    }
    //编辑词（ordinary）回显信息
    getSingleOrdWord(dictionaryId,wordId){
        let { wordOrdinary } = this.state;
        let word,nature,frequency,dicId_ord,wordId_ord,wordLabel;
        wordOrdinary.map(function (item) {
            if(item.dictionaryId == dictionaryId && item.wordId == wordId){
                dicId_ord = item.dictionaryId;
                wordId_ord = item.wordId;
                word = item.word;
                wordLabel = item.wordLabel;
                nature = item.nature;
                frequency = item.frequency;
            }
        });
        this.setState({word,nature,frequency,dicId_ord,wordId_ord,wordLabel});
    }
    //编辑词（map）回显信息
    getSingleMapWord(dictionaryId,wordId){
        let { wordMap } = this.state;
        let wordKey,wordValue,dicId_map,wordId_map;
        wordMap.map(function (item) {
            if(item.dictionaryId == dictionaryId && item.wordId == wordId){
                dicId_map = item.dictionaryId;
                wordId_map = item.wordId;
                wordKey = item.wordKey;
                wordValue = item.wordValue;
            }
        });
        this.setState({wordKey,wordValue,dicId_map,wordId_map});
    }

    //编辑词（sentiment）模态框
    updateSenWordModal(dictionaryId,wordId){
        this.getSingleSenWord(dictionaryId,wordId);
        this.setState({updateSenWordVisible : true});
    }
    closeUpdateSenWord(){
        this.setState({updateSenWordVisible : false});
    }
    //编辑词（sentiment）回显信息
    getSingleSenWord(dictionaryId,wordId){
        let { wordSentiment } = this.state;
        let word_sen,nature_sen,grade,dicId_sen,wordId_sen;
        wordSentiment.map(function (item) {
            if(item.dictionaryId == dictionaryId && item.wordId == wordId){
                dicId_sen = item.dictionaryId;
                wordId_sen = item.wordId;
                word_sen = item.word;
                nature_sen = item.nature;
                grade = item.grade;
            }
        });
        this.setState({word_sen,nature_sen,grade,dicId_sen,wordId_sen});
    }

    //停用词
    stopWord(dicId,wordId){
        fetch(Uniplatform.context.url + '/nlap/admin/dicInfo/word/stop?dicId='+dicId+'&wordIds='+wordId, {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else{
                    popup(<Snackbar message={data.msg}/>);
                    this.getFetchWordList('');
                }
            }).catch((err) => console.log(err.toString()));
    }
    //启用词
    startWord(dicId,wordId){
        fetch(Uniplatform.context.url + '/nlap/admin/dicInfo/word/start?dicId='+dicId+'&wordIds='+wordId, {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else{
                    popup(<Snackbar message={data.msg}/>);
                    this.getFetchWordList('');
                }
            }).catch((err) => console.log(err.toString()));
    }

    //创建词库校验
    beforeDicSubmit(data){
        let {isField} = this.state;
        let dicName = data.dicName.replace(/(^\s*)|(\s*$)/g, "");
        if(!dicName){
            popup(<Snackbar message="词库名称不能为空"/>);
            return false;
        }else if (!data.dicTypeid){
            popup(<Snackbar message="词库类别不能为空"/>);
            return false;
        }else if (!data.dicSubTypeId){
            popup(<Snackbar message="词库类型不能为空"/>);
            return false;
        }else if (isField && (!data.fieldTypeId)){
            popup(<Snackbar message="功能分类不能为空"/>);
            return false;
        }else {
            this.setState({progressBool : true});
            let di = 'dicName';
            data[di] = dicName;
            return data;
        }
    }

    //上传词校验
    beforeUploadSubmit(data){
        if(data.file.length == 0){
            popup(<Snackbar message="上传文件不能为空"/>);
            return false;
        }else if(data.file.length > 1){
            popup(<Snackbar message="一次只能上传一个文件"/>);
            return false;
        }else {
            this.setState({progress_bool : true});
            return true;
        }
    }

    //添加词校验（ordinary）
    beforeWordSubmit(data){
        let ide_value = '';
        if(this.state.fieldSubType == '人'){
            ide_value = document.getElementById('ide_value').value;
        }
        let word = data.word.replace(/(^\s*)|(\s*$)/g, "");
        let frequency = data.frequency.replace(/(^\s*)|(\s*$)/g, "");
        if(!word){
            popup(<Snackbar message="词名称不能为空"/>);
            return false;
        }else if (!frequency){
            popup(<Snackbar message="词频不能为空"/>);
            return false;
        }else if (!(/^\d*$/g.test(data.frequency))){
            popup(<Snackbar message="词频必须为数字"/>);
            return false;
        }else {
            let labelValue = 'wordLabel';
            data[labelValue] = ide_value;
            let w = 'word';
            data[w] = word;
            let f = 'frequency';
            data[f] = frequency;
            return data;
        }
    }
    //编辑词校验（ordinary）
    beforeWordUpdateSubmit(data){
        let ide_editValue = '';
        if(this.state.fieldSubType == '人'){
            ide_editValue = document.getElementById('ide_editValue').value;
        }
        if (!data.newFrequency){
            popup(<Snackbar message="词频不能为空"/>);
            return false;
        }else if (!(/^\d*$/g.test(data.newFrequency))){
            popup(<Snackbar message="词频必须为数字"/>);
            return false;
        }else {
            let labelValue = 'wordLabel';
            data[labelValue] = ide_editValue;
            return data;
        }
    }

    //添加词校验（sentiment）
    beforeSenWordSubmit(data){
        let word = data.word.replace(/(^\s*)|(\s*$)/g, "");
        let grade = data.grade.replace(/(^\s*)|(\s*$)/g, "");
        if(!word){
            popup(<Snackbar message="词名称不能为空"/>);
            return false;
        }else if (!grade){
            popup(<Snackbar message="评分不能为空"/>);
            return false;
        }else if (!(/^\d*$/g.test(data.grade))){
            popup(<Snackbar message="评分必须为数字"/>);
            return false;
        }else if ((data.grade<0) || (data.grade>1)){
            popup(<Snackbar message="评分必须为0-1的数字"/>);
            return false;
        }else {
            let w = 'word';
            data[w] = word;
            let g = 'grade';
            data[g] = grade;
            return data;
        }
    }
    //编辑词校验（sentiment）
    beforeUpdateSenWordSubmit(data){
        let newWord = data.newWord.replace(/(^\s*)|(\s*$)/g, "");
        if(!newWord){
            popup(<Snackbar message="词名称不能为空"/>);
            return false;
        }else if (!data.grade){
            popup(<Snackbar message="评分不能为空"/>);
            return false;
        }else if (!(/^\d*$/g.test(data.grade))){
            popup(<Snackbar message="评分必须为0-1的数字"/>);
            return false;
        }else if ((data.grade<0) || (data.grade>1)){
            popup(<Snackbar message="评分必须为0-1的数字"/>);
            return false;
        }else {
            return true;
        }
    }

    //添加词校验（map）
    beforeMapWordSubmit(data){
        let wordKey = data.wordKey.replace(/(^\s*)|(\s*$)/g, "");
        if(!wordKey){
            popup(<Snackbar message="键不能为空"/>);
            return false;
        }else {
            let w = 'wordKey';
            data[w] = wordKey;
            return data;
        }
    }

    afterSubmit(data){
        popup(<Snackbar message={data.msg} />);
        if(data.status == 200){
            this.resetEnabled();
            this.resetUpdateDic();
            this.resetEdit();
            this.setState({updateDicVisible : false,dicLabelVisible:false});
        }
        this.getFetchAllDic();
    }
    afterAddDicSubmit(data){
        let {isField} = this.state;
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            if(data.status == 200){
                if(isField){
                    popup(<Snackbar message={data.info} />);
                }else {
                    popup(<Snackbar message={data.wordAllNum} />);
                }
                this.setState({addDicVisible:false,index:1,dicType:undefined,subType:undefined,
                    labelType:undefined,backDicSubTypeIndex:undefined,backLabelIndex:undefined,
                    backDicIndex:undefined});
                this.reset();
                this.resetFile();
                this.getFetchDictionaries();
                this.getFetchAllDic();
            }else{
                popup(<Snackbar message={data.msg} />);
            }
        }
        this.setState({progressBool : false});
    }
    afterAddSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.setState( {addOrdWordVisible: false,uploadWordVisible: false,
                    addMapWordVisible: false, addSenWordVisible: false,
                    updateOrdWordVisible: false, updateMapWordVisible: false,
                    updateSenWordVisible: false} );
                this.resetUpload();
                this.resetAddMap();
                this.resetAddOrd();
                this.resetAddSen();
                this.formReset();
                this.getFetchWordList('');
            }
        }
        this.setState({progress_bool : false});
    }

    //双击词库名称变成输入框（修改词库名称）
    updateDicName(loc,dicId,dicName){
        let inputHtml = [];
        inputHtml.push(
            <div key={loc}>
                <Input placeholder="请输入词库名" value={dicName} style={{width:'70%',display:'inline-block'}} onChange={ this.handleChange.bind(this) }/>
                &nbsp;&nbsp;<a onClick={this.getFetchUpdateDicName.bind(this,dicId)}><i className="fa fa-check"></i></a>&nbsp;&nbsp;
                <a onClick={this.closeInput.bind(this)}><i className="fa fa-times"></i></a>
            </div>
        );
        this.setState({inputHtml,loc});
        //this.dictionaryList();
        this.getFetchAllDic();
    }
    closeInput(){
        this.setState({inputHtml : []});
        //this.dictionaryList();
        this.getFetchAllDic();
    }

    handleChange( data ) {
        this.setState( { newDictName: data } );
    }

    //修改词库名称
    getFetchUpdateDicName(dicId){
        let {newDictName} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/dicMgmt/update/name?dictId='+dicId+'&newDicName='+newDictName, {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else {
                    popup(<Snackbar message={data.msg}/>);
                    if (data.status == 200){
                        this.setState({inputHtml : []});
                        this.getFetchAllDic();
                    }
                }
            }).catch((err) => console.log(err.toString()));
    }

    //删除词库
    deleteDic(dicId){
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleApprove.bind(this,dicId)
        };
        popup(<Dialog style={{top:'15%'}}
                      message="确定要删除该词库?"
                      type="alert"
                      showCancelBtn
                      cancelBtn={ cancelBtn }
                      showApproveBtn
                      approveBtn = { approveBtn }
        />);
    }
    handleApprove(dicId,after) {
        fetch(Uniplatform.context.url + '/nlap/admin/dicMgmt/dict/remove?dictId='+dicId, {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }
                if (data.status == 200){
                    this.setState({index:1});
                    this.getFetchDictionaries();
                    this.getFetchAllDic();
                    popup(<Snackbar message={data.msg}/>);
                } else {
                    popup(<Snackbar message={data.msg}/>);
                }
                after( true );
            }).catch((err) => {
            console.log(err.toString());
            after( true );
        });
    }

    //上传词模态框
    uploadWordModal(format){
        this.setState({ uploadWordVisible: true, upload_format : format });
    }
    closeUploadWord(){
        this.resetUpload();
        this.setState({ uploadWordVisible: false });
    }

    //删除词
    deleteWord(dicId,workId){
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleWordApprove.bind(this,dicId,workId)
        };
        popup(<Dialog style={{top:'15%'}}
                      message="确定要删除该词?"
                      type="alert"
                      showCancelBtn
                      cancelBtn={ cancelBtn }
                      showApproveBtn
                      approveBtn = { approveBtn }
        />);
    }
    handleWordApprove(dicId,workId,after) {
        fetch(Uniplatform.context.url + '/nlap/admin/dicInfo/word/delete?dicId='+dicId+'&wordIds='+workId, {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                let {message} = this.state;
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else {
                    if (data.status == 200){
                        if(message){
                            popup(<Snackbar message={message}/>);
                        }else {
                            popup(<Snackbar message={data.msg}/>);
                        }
                        this.setState({index_table:1});
                        this.getFetchWordList('');
                    }else {
                        popup(<Snackbar message={data.msg}/>);
                    }
                }
                after( true );
            }).catch((err) => {
            console.log(err.toString());
            after( true );
        });
    }

    //批量删除词
    deleteWords(dictionaryId,select,wordStatus){
        this.setState({ message : undefined});
        if(select.length == 0){
            popup(<Snackbar message="您未选择任何词！"/>);
        }else if((wordStatus.length ==1) &&(wordStatus != 0)){
            popup(<Snackbar message="该词正在使用中！"/>);
        }else if(wordStatus.length > 1){
            for(let i=0; i<wordStatus.length; i++){
                if(wordStatus[i] != 0){
                    this.setState({ message : "所选词中正在被使用的词不能删除，其余已删除成功"});
                }
            }
            this.deleteWord(dictionaryId,select);
        }else {
            this.deleteWord(dictionaryId,select);
        }
    }

    //点击词库类型
    clickDicType(dicTypeName,dicTypeId,index){
        let content = document.getElementsByClassName('dicManage_content_col');
        /*let link = document.getElementsByClassName('aType');
         let sub_link = document.getElementsByClassName('subType');
         let lable_link = document.getElementsByClassName('lType');*/

        for(let i=0;i<content.length;i++){
            content[i].style.backgroundColor = null;
            content[i].style.border = null;
        }
        /*for(let i=0;i<link.length;i++){
         link[i].style.backgroundColor = null;
         }

         for(let i=0;i<sub_link.length;i++){
         sub_link[i].style.backgroundColor = null;
         }
         sub_link[0].style.backgroundColor = '#dcdcdc';

         for(let i=0;i<lable_link.length;i++){
         lable_link[i].style.backgroundColor = null;
         }
         lable_link[0].style.backgroundColor = '#dcdcdc';*/

        content[index].style.backgroundColor = '#ceeefc';
        content[index].style.border = "solid 1px #6fccf5";
        //link[index].style.backgroundColor = '#dcdcdc';
        this.getDicType(dicTypeName);
        this.setState({backDicIndex : index,backDicTypeName : dicTypeName,backDicTypeId : dicTypeId,
                backLabelIndex:undefined,backDicSubTypeIndex:undefined,index:1,dicType:dicTypeId,
                subType:undefined,labelType:undefined,searchDic:undefined},
            ()=>this.getFetchAllDic());
        this.resetSearch();
    }

    //点击标签类型
    clickLabelType(dicTypeId,dicSubTypeId,labelTypeId,index){
        let lable_link = document.getElementsByClassName('lType');
        for(let i=0;i<lable_link.length;i++){
            lable_link[i].style.backgroundColor = null;
        }
        lable_link[index].style.backgroundColor = '#dcdcdc';
        this.setState({backLabelIndex : index,backDicTypeId : dicTypeId,backDicSubTypeId : dicSubTypeId,
            backLabelTypeId : labelTypeId,index:1,dicType:dicTypeId,subType:dicSubTypeId,labelType:labelTypeId,searchDic:undefined},
            ()=>this.getFetchAllDic());
    }

    //重置表单
    formTirgger( trigger ) {
        this.reset = trigger.reset;
    }
    formFileTirgger( trigger ) {
        this.resetFile = trigger.reset;
    }
    formUploadTirgger(trigger){
        this.resetUpload = trigger.reset;
    }
    formAddMapTirgger( trigger ) {
        this.resetAddMap = trigger.reset;
    }
    formAddOrdTirgger( trigger ) {
        this.resetAddOrd = trigger.reset;
    }
    formAddSenTirgger( trigger ){
        this.resetAddSen = trigger.reset;
    }
    triggerEnabled(obj){
        this.resetEnabled = obj.reset;
    }
    triggerUpdateDic(obj){
        this.resetUpdateDic = obj.reset;
    }
    formEditTirgger( trigger ) {
        this.resetEdit = trigger.reset;
    }

    //添加词库时获取领域词库类型
    getFetchFieldType(subTypeId){
        let {add_typeId} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/dicMgmt/field/type/list?typeId='+add_typeId+'&subTypeId='+subTypeId, {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let fieldType = data.fieldTypes.map(function (item) {
                    let field = {value:item.id,text:item.name};
                    return field;
                });
                this.setState({fieldType});
            }).catch((err) => console.log(err.toString()));
    }

    //领域词库给词库打标签
    showAddLabel(dictionaryId){
        this.getFetchLablesList();
        this.getFetchLablesEchoList(dictionaryId);
        this.setState({ addLabelVisible : true, dictionaryId:dictionaryId });
    }
    closeAddLabel(){
        this.resetAddLabel();
        this.setState({ addLabelVisible : false });
    }
    formAddLabelTirgger( trigger ) {
        this.resetAddLabel = trigger.reset;
    }
    beforeAddLabelSubmit(data){
        if(data.labelIds.length == 0){
            let labelIds = 'labelIds';
            data[labelIds] = '';
        }
        return data;
    }
    afterAddLabelSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message}/>);
        }else {
            popup(<Snackbar message={data.msg}/>);
            if(data.status == 200){
                this.resetAddLabel();
                this.getFetchWordList('');
                this.setState({ addLabelVisible : false });
            }
        }
    }
    //获取标签列表
    getFetchLablesList(){
        let {index,size} = this.state;
        let param = new FormData();
        param.append('pageIndex',1);
        param.append('pageSize',10000);
        param.append('keyword','');
        param.append('ascSort',false);
        fetch(Uniplatform.context.url + '/nlap/admin/label/mgmt/labels/list',{
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res)=>res.json())
            .then((data)=>{
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let labelsList = data.labels.labels.map(function (item) {
                    let la = {value : item.id, text : item.name};
                    return la;
                });
                this.setState({labelsList});
            }).catch((err)=>console.log(err.toString()));
    }
    //获取词库已有标签（回显）
    getFetchLablesEchoList(dicId){
        fetch(Uniplatform.context.url + '/nlap/admin/dicMgmt/dic/labels/info?dicId='+dicId,{
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res)=>res.json())
            .then((data)=>{
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let labelsEchoList = [];
                data.lables.map(function (item) {
                    labelsEchoList.push(item.labelId);
                });
                this.setState({labelsEchoList});
            }).catch((err)=>console.log(err.toString()));
    }

    //领域词库给词打标签
    showAddWordLabel(wordId){
        this.getFetchLablesList();
        this.getFetchWordLablesEchoList(wordId);
        this.setState({ addWordLabelVisible : true, wordId:wordId });
    }
    closeAddWordLabel(){
        this.resetAddWordLabel();
        this.setState({ addWordLabelVisible : false });
    }
    formAddWordLabelTirgger( trigger ) {
        this.resetAddWordLabel = trigger.reset;
    }
    beforeAddWordLabelSubmit(data){
        if(data.labelIds.length == 0){
            let labelIds = 'labelIds';
            data[labelIds] = '';
        }
        return data;
    }
    afterAddWordLabelSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message}/>);
        }else {
            popup(<Snackbar message={data.msg}/>);
            if(data.status == 200){
                this.resetAddWordLabel();
                this.getFetchWordList('');
                this.setState({ addWordLabelVisible : false });
            }
        }
    }
    //获取词已有标签（回显）
    getFetchWordLablesEchoList(wordId){
        fetch(Uniplatform.context.url + '/nlap/admin/dicInfo/word/labels/info?wordId='+wordId,{
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res)=>res.json())
            .then((data)=>{
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let labelsWordEchoList = [];
                data.lables.map(function (item) {
                    labelsWordEchoList.push(item.labelId);
                });
                this.setState({labelsWordEchoList});
            }).catch((err)=>console.log(err.toString()));
    }
    getName(e){
        let value=e.target.value;
        this.setState({wordLabel: value});
    }


    formReset()
    {
        document.getElementById("myForm").reset();
    }

    //重置搜索框
    formSearchTirgger(trigger){
        this.resetSearch = trigger.reset;
    }

    //添加词库模态框中 重置词库子类型
    formDicTypeTirgger(trigger){
        this.resetDicType = trigger.reset;
    }

    //添加词库模态框中 重置词库功能分类（领域词库）
    formFieldTypeTirgger(trigger){
        this.resetFieldType = trigger.reset;
    }

    render() {
        let {dicTypeId,dicTypeNameData,dicNumberData,addMapWordVisible,addOrdWordVisible,addDicVisible,addType,
            addSubType,addLabelType,isWrite,dicLabelVisible,leftList,rightList,dicId,selectLabelId,updateMapWordVisible,
            updateOrdWordVisible,word,nature,frequency,dicId_ord,wordId_ord,wordLabel,wordKey,wordValue,dicId_map,
            wordId_map,updateDicVisible,dictionaryId,dicName,unusedList,usedList,dictId,uploadWordVisible,newLabelId,
            addFormat,upload_format,isDicDetail,natureNameList,dicName_detail,backDicIndex,addSenWordVisible,
            word_sen,nature_sen,grade,dicId_sen,wordId_sen,updateSenWordVisible,isField,fieldType,dicType,
            addLabelVisible,labelsList,addWordLabelVisible,wordId,labelsWordEchoList,labelsEchoList,progressBool,
            progress_bool} = this.state;
        return (
            <Page>
                {/*<COMM_HeadBanner prefix=" " />
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>*/}
                <Row style={{minHeight:'680px'}}>
                    {/*<Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'910px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <NLPMenu url={'/nlap/platform/dictionary'}/>
                        </div>
                    </Col>*/}
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{padding:'0',marginTop:'20px'}}>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className = "dicManage_span">词库管理</span>
                                </div>
                            </Col>
                            {isDicDetail == false ?
                                (backDicIndex ?
                                        <div className="dicManage_content">
                                            {backDicIndex == 0 ?
                                                <a onClick={this.clickDicType.bind(this,dicTypeNameData[0],dicTypeId[0],0)}>
                                                    <div style={{display:'inline-block',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                                        <img src="../image/dic_icon1.png" style={{margin:'7%',display:'inline-block'}}/>
                                                        <span className="statistic" style={{color:'#da8b5c'}}>
                                                            {dicTypeNameData[0]}<br/>({dicNumberData[0]})
                                                        </span>
                                                    </div>
                                                </a>
                                                :
                                                <a onClick={this.clickDicType.bind(this,dicTypeNameData[0],dicTypeId[0],0)}>
                                                    <div style={{display:'inline-block'}} className="dicManage_content_col">
                                                        <img src="../image/dic_icon1.png" style={{margin:'7%',display:'inline-block'}}/>
                                                        <span className="statistic" style={{color:'#da8b5c'}}>
                                                            {dicTypeNameData[0]}<br/>({dicNumberData[0]})
                                                        </span>
                                                    </div>
                                                </a>
                                            }
                                            {backDicIndex == 1 ?
                                                <a onClick={this.clickDicType.bind(this,dicTypeNameData[1],dicTypeId[1],1)}>
                                                    <div style={{display:'inline-block',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                                        <img src="../image/dic_icon2.png" style={{margin:'7%',display:'inline-block'}}/>
                                                        <span className="statistic" style={{color:'#84c7e5'}}>
                                                    {dicTypeNameData[1]}<br/>({dicNumberData[1]})
                                                </span>
                                                    </div>
                                                </a>
                                                :
                                                <a onClick={this.clickDicType.bind(this,dicTypeNameData[1],dicTypeId[1],1)}>
                                                    <div style={{display:'inline-block'}} className="dicManage_content_col">
                                                        <img src="../image/dic_icon2.png" style={{margin:'7%',display:'inline-block'}}/>
                                                        <span className="statistic" style={{color:'#84c7e5'}}>
                                                    {dicTypeNameData[1]}<br/>({dicNumberData[1]})
                                                </span>
                                                    </div>
                                                </a>
                                            }
                                            {backDicIndex == 2 ?
                                                <a onClick={this.clickDicType.bind(this,dicTypeNameData[2],dicTypeId[2],2)}>
                                                    <div style={{display:'inline-block',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                                        <img src="../image/dic_icon3.png" style={{margin:'7%',display:'inline-block'}}/>
                                                        <span className="statistic" style={{color:'#98d37b'}}>
                                                    {dicTypeNameData[2]}<br/>({dicNumberData[2]})
                                                </span>
                                                    </div>
                                                </a>
                                                :
                                                <a onClick={this.clickDicType.bind(this,dicTypeNameData[2],dicTypeId[2],2)}>
                                                    <div style={{display:'inline-block'}} className="dicManage_content_col">
                                                        <img src="../image/dic_icon3.png" style={{margin:'7%',display:'inline-block'}}/>
                                                        <span className="statistic" style={{color:'#98d37b'}}>
                                                    {dicTypeNameData[2]}<br/>({dicNumberData[2]})
                                                </span>
                                                    </div>
                                                </a>
                                            }
                                            {backDicIndex == 3 ?
                                                <a onClick={this.clickDicType.bind(this,dicTypeNameData[3],dicTypeId[3],3)}>
                                                    <div style={{display:'inline-block',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                                        <img src="../image/dic_icon4.png" style={{margin:'7%',display:'inline-block'}}/>
                                                        <span className="statistic" style={{color:'#ff838d'}}>
                                                    {dicTypeNameData[3]}<br/>({dicNumberData[3]})
                                                </span>
                                                    </div>
                                                </a>
                                                :
                                                <a onClick={this.clickDicType.bind(this,dicTypeNameData[3],dicTypeId[3],3)}>
                                                    <div style={{display:'inline-block'}} className="dicManage_content_col">
                                                        <img src="../image/dic_icon4.png" style={{margin:'7%',display:'inline-block'}}/>
                                                        <span className="statistic" style={{color:'#ff838d'}}>
                                                    {dicTypeNameData[3]}<br/>({dicNumberData[3]})
                                                </span>
                                                    </div>
                                                </a>
                                            }
                                        </div>
                                        :
                                        <div className="dicManage_content">
                                            <a onClick={this.clickDicType.bind(this,dicTypeNameData[0],dicTypeId[0],0)}>
                                                <div style={{display:'inline-block',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                                    <img src="../image/dic_icon1.png" style={{margin:'7%',display:'inline-block'}}/>
                                                    <span className="statistic" style={{color:'#da8b5c'}}>
                                                    {dicTypeNameData[0]}<br/>({dicNumberData[0]})
                                                </span>
                                                </div>
                                            </a>
                                            <a onClick={this.clickDicType.bind(this,dicTypeNameData[1],dicTypeId[1],1)}>
                                                <div style={{display:'inline-block'}} className="dicManage_content_col">
                                                    <img src="../image/dic_icon2.png" style={{margin:'7%',display:'inline-block'}}/>
                                                    <span className="statistic" style={{color:'#84c7e5'}}>
                                                    {dicTypeNameData[1]}<br/>({dicNumberData[1]})
                                                </span>
                                                </div>
                                            </a>
                                            <a onClick={this.clickDicType.bind(this,dicTypeNameData[2],dicTypeId[2],2)}>
                                                <div style={{display:'inline-block'}} className="dicManage_content_col">
                                                    <img src="../image/dic_icon3.png" style={{margin:'7%',display:'inline-block'}}/>
                                                    <span className="statistic" style={{color:'#98d37b'}}>
                                                    {dicTypeNameData[2]}<br/>({dicNumberData[2]})
                                                </span>
                                                </div>
                                            </a>
                                            <a onClick={this.clickDicType.bind(this,dicTypeNameData[3],dicTypeId[3],3)}>
                                                <div style={{display:'inline-block'}} className="dicManage_content_col">
                                                    <img src="../image/dic_icon4.png" style={{margin:'7%',display:'inline-block'}}/>
                                                    <span className="statistic" style={{color:'#ff838d'}}>
                                                    {dicTypeNameData[3]}<br/>({dicNumberData[3]})
                                                </span>
                                                </div>
                                            </a>
                                        </div>


                                )
                                :
                                <div className="dicManage_content">
                                    {backDicIndex ?
                                        (
                                            backDicIndex == 0 ?
                                                <div style={{display:'inline-block',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                                    <img src="../image/dic_icon1.png" style={{margin:'7%',display:'inline-block'}}/>
                                                    <span className="statistic" style={{color:'#da8b5c'}}>
                                                        {dicTypeNameData[0]}<br/>({dicNumberData[0]})
                                                    </span>
                                                </div>
                                                :
                                                <div style={{display:'inline-block'}} className="dicManage_content_col">
                                                    <img src="../image/dic_icon1.png" style={{margin:'7%',display:'inline-block'}}/>
                                                    <span className="statistic" style={{color:'#da8b5c'}}>
                                                        {dicTypeNameData[0]}<br/>({dicNumberData[0]})
                                                    </span>
                                                </div>
                                        )
                                        :
                                        <div style={{display:'inline-block',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                            <img src="../image/dic_icon1.png" style={{margin:'7%',display:'inline-block'}}/>
                                            <span className="statistic" style={{color:'#da8b5c'}}>
                                                {dicTypeNameData[0]}<br/>({dicNumberData[0]})
                                            </span>
                                        </div>
                                    }
                                    {backDicIndex == 1 ?
                                        <div style={{display:'inline-block',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                            <img src="../image/dic_icon2.png" style={{margin:'7%',display:'inline-block'}}/>
                                            <span className="statistic" style={{color:'#84c7e5'}}>
                                                {dicTypeNameData[1]}<br/>({dicNumberData[1]})
                                            </span>
                                        </div>
                                        :
                                        <div style={{display:'inline-block'}} className="dicManage_content_col">
                                            <img src="../image/dic_icon2.png" style={{margin:'7%',display:'inline-block'}}/>
                                            <span className="statistic" style={{color:'#84c7e5'}}>
                                                {dicTypeNameData[1]}<br/>({dicNumberData[1]})
                                            </span>
                                        </div>
                                    }
                                    {backDicIndex == 2 ?
                                        <div style={{display:'inline-block',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                            <img src="../image/dic_icon3.png" style={{margin:'7%',display:'inline-block'}}/>
                                            <span className="statistic" style={{color:'#98d37b'}}>
                                                {dicTypeNameData[2]}<br/>({dicNumberData[2]})
                                            </span>
                                        </div>
                                        :
                                        <div style={{display:'inline-block'}} className="dicManage_content_col">
                                            <img src="../image/dic_icon3.png" style={{margin:'7%',display:'inline-block'}}/>
                                            <span className="statistic" style={{color:'#98d37b'}}>
                                                {dicTypeNameData[2]}<br/>({dicNumberData[2]})
                                            </span>
                                        </div>
                                    }
                                    {backDicIndex == 3 ?
                                        <div style={{display:'inline-block',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                            <img src="../image/dic_icon4.png" style={{margin:'7%',display:'inline-block'}}/>
                                            <span className="statistic" style={{color:'#ff838d'}}>
                                                {dicTypeNameData[3]}<br/>({dicNumberData[3]})
                                            </span>
                                        </div>
                                        :
                                        <div style={{display:'inline-block'}} className="dicManage_content_col">
                                            <img src="../image/dic_icon4.png" style={{margin:'7%',display:'inline-block'}}/>
                                            <span className="statistic" style={{color:'#ff838d'}}>
                                                {dicTypeNameData[3]}<br/>({dicNumberData[3]})
                                            </span>
                                        </div>
                                    }
                                </div>
                            }

                        </Row>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className = "dicManage_span">词库列表 { dicName_detail ? '> '+dicName_detail+(this.state.fieldSubType ? '(领域词库-'+this.state.fieldSubType+')' : '') : '' }</span>
                                </div>
                            </Col>
                            {this.showAllDictionaries()}
                        </Row>
                    </Col>


                    <Form type="horizontal"
                          method="post"
                          action={contextUrl + '/nlap/admin/dicInfo/word/add/ordinary'}
                          trigger={ this.formAddOrdTirgger }
                          async={true}
                          onSubmit={this.beforeWordSubmit}
                          onAfterSubmit={this.afterAddSubmit}
                    >
                        <Modal visible={ addOrdWordVisible } size="middle" onClose={ this.closeAddWord.bind( this ) }>
                            <ModalHeader>
                                添加词（普通词）
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{ display:'none' }} name="dicId" value={dictionaryId}/>
                                <FormItem>
                                    <Label>词名称</Label>
                                    <Input name="word" placeholder="请输入词名称" />
                                </FormItem>

                                <form id="myForm">
                                    {this.state.fieldSubType == '人' ?
                                        <FormItem>
                                            <Label>词标签</Label>
                                            <input type="text" id="ide_value" style={{width:'82%'}} list="ide" name="wordLabel" placeholder="请输入词标签" className="epm select dropdown-trigger"/>
                                            <datalist id="ide">
                                                {
                                                    labelsList.map(function (item) {
                                                        return (<option key={item.text} style={{color:'red'}} value={item.text} />);
                                                    })
                                                }
                                            </datalist>
                                        </FormItem>
                                        : ''
                                    }

                                </form>

                                <FormItem>
                                    <Label>词属性</Label>
                                    <Select dataSource={natureNameList} name="nature" placeholder="请选择词属性"/>
                                </FormItem>
                                <FormItem>
                                    <Label>词频</Label>
                                    <Input name="frequency" placeholder="请输入数字"/>
                                </FormItem>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeAddWord.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>
                    <Form type="horizontal"
                          method="post"
                          action={contextUrl + '/nlap/admin/dicInfo/word/edit/ordinary'}
                          async={true}
                          onSubmit={this.beforeWordUpdateSubmit}
                          onAfterSubmit={this.afterAddSubmit}
                    >
                        <Modal visible={ updateOrdWordVisible } size="middle" onClose={ this.closeUpdateOrdWord.bind( this ) }>
                            <ModalHeader>
                                编辑词（普通词）
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{ display:'none' }} name="dicId" value={dicId_ord}/>
                                <Input style={{ display:'none' }} name="wordId" value={wordId_ord}/>
                                <FormItem>
                                    <Label>词名称</Label>
                                    <Input name="newWord" value={word} placeholder="请输入词名称"/>
                                </FormItem>
                                {this.state.fieldSubType == '人' ?
                                    <FormItem>
                                        <Label>词标签</Label>
                                        <input type="text" id="ide_editValue" value={wordLabel} style={{width:'82%'}} list="ide_edit"
                                               name="wordLabel" placeholder="请输入词标签" className="epm select dropdown-trigger"
                                               onChange={this.getName.bind(this)}/>
                                        <datalist id="ide_edit">
                                            {
                                                labelsList.map(function (item) {
                                                    return (<option key={item.text} style={{color:'red'}} value={item.text} />);
                                                })
                                            }
                                        </datalist>
                                    </FormItem>
                                    : ''
                                }
                                <FormItem>
                                    <Label>词属性</Label>
                                    <Select dataSource={natureNameList} value={nature} name="newNature" placeholder="请选择词属性"/>
                                </FormItem>
                                <FormItem>
                                    <Label>词频</Label>
                                    <Input name="newFrequency" placeholder="请输入数字" value={frequency}/>
                                </FormItem>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeUpdateOrdWord.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>

                    <Form type="horizontal"
                          method="post"
                          action={contextUrl + '/nlap/admin/dicInfo/word/add/sentiment'}
                          trigger={ this.formAddSenTirgger }
                          async={true}
                          onSubmit={this.beforeSenWordSubmit}
                          onAfterSubmit={this.afterAddSubmit}
                    >
                        <Modal visible={ addSenWordVisible } size="middle" onClose={ this.closeAddWord.bind( this ) }>
                            <ModalHeader>
                                添加词（情感词）
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{ display:'none' }} name="dicId" value={dictionaryId}/>
                                <FormItem>
                                    <Label>词名称</Label>
                                    <Input name="word" placeholder="请输入词名称" />
                                </FormItem>
                                <FormItem>
                                    <Label>词属性</Label>
                                    <Select dataSource={natureNameList} name="nature" placeholder="请选择词属性"/>
                                </FormItem>
                                <FormItem>
                                    <Label>评分</Label>
                                    <Input name="grade" placeholder="请输入0-1的数字" />
                                </FormItem>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeAddWord.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>
                    <Form type="horizontal"
                          method="post"
                          action={contextUrl + '/nlap/admin/dicInfo/word/edit/sentiment'}
                          async={true}
                          onSubmit={this.beforeUpdateSenWordSubmit}
                          onAfterSubmit={this.afterAddSubmit}
                    >
                        <Modal visible={ updateSenWordVisible } size="middle" onClose={ this.closeUpdateSenWord.bind( this ) }>
                            <ModalHeader>
                                编辑词（情感词）
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{ display:'none' }} name="dicId" value={dicId_sen}/>
                                <Input style={{ display:'none' }} name="wordId" value={wordId_sen}/>
                                <FormItem>
                                    <Label>词名称</Label>
                                    <Input name="newWord" value={word_sen} placeholder="请输入词名称"/>
                                </FormItem>
                                <FormItem>
                                    <Label>词属性</Label>
                                    <Select dataSource={natureNameList} value={nature_sen} name="newNature" placeholder="请选择词属性"/>
                                </FormItem>
                                <FormItem>
                                    <Label>评分</Label>
                                    <Input name="grade" placeholder="请输入0-1的数字" value={grade}/>
                                </FormItem>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeUpdateSenWord.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>

                    <Form type="horizontal"
                          method="post"
                          action={contextUrl + '/nlap/admin/dicInfo/word/add/map'}
                          trigger={ this.formAddMapTirgger }
                          async={true}
                          onSubmit={this.beforeMapWordSubmit}
                          onAfterSubmit={this.afterAddSubmit}
                    >
                        <Modal visible={ addMapWordVisible } size="middle" onClose={ this.closeAddWord.bind( this ) }>
                            <ModalHeader>
                                添加词（键值对）
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{ display:'none' }} name="dicId" value={dictionaryId}/>
                                <FormItem>
                                    <Label>键</Label>
                                    <Input name="wordKey" placeholder="请输入键" />
                                </FormItem>
                                <FormItem>
                                    <Label>值</Label>
                                    <Input name="wordValue" placeholder="请输入值"/>
                                </FormItem>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeAddWord.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>
                    <Form type="horizontal"
                          method="post"
                          action={contextUrl + '/nlap/admin/dicInfo/word/edit/map'}
                          async={true}
                          onAfterSubmit={this.afterAddSubmit}
                    >
                        <Modal visible={ updateMapWordVisible } size="middle" onClose={ this.closeUpdateMapWord.bind( this ) }>
                            <ModalHeader>
                                编辑词（键值对）
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{ display:'none' }} name="dicId" value={dicId_map}/>
                                <Input style={{ display:'none' }} name="wordId" value={wordId_map}/>
                                <FormItem>
                                    <Label>键</Label>
                                    <Input name="newKey" value={wordKey}/>
                                </FormItem>
                                <FormItem>
                                    <Label>值</Label>
                                    <Input name="newValue" placeholder="请输入值" value={wordValue}/>
                                </FormItem>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeUpdateMapWord.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>

                    <Form type="horizontal"
                          method="post"
                          action={isField ? contextUrl +'/nlap/admin/dicMgmt/field/dict/add' : contextUrl + '/nlap/admin/dicMgmt/dict/add'}
                          trigger={ this.formTirgger }
                          async={true}
                          enctype="multipart/form-data"
                          onSubmit={this.beforeDicSubmit}
                          onAfterSubmit={this.afterAddDicSubmit}
                    >
                        <Modal visible={ addDicVisible } size="middle" onClose={ this.closeAddDicModal.bind( this ) }>
                            <ModalHeader>
                                添加词库
                            </ModalHeader>
                            <ModalBody>
                                <FormItem>
                                    <Label>词库名称</Label>
                                    <Input name="dicName" placeholder="请输入词库名称" />
                                </FormItem>
                                <FormItem>
                                    <Label>词库类别</Label> {/*词库类型*/}
                                    <Select dataSource={addType} /*value={dicType}*/ name="dicTypeid" placeholder="请选择词库类型" onChange={this.dicTypeChange.bind(this)} showClear={ false }/>
                                </FormItem>
                                <FormItem disabled={isWrite}>
                                    <Label>词库类型</Label> {/*词库子类型*/}
                                    <Select dataSource={addSubType} name="dicSubTypeId" placeholder="请选择词库子类型" trigger={this.formDicTypeTirgger.bind(this)}
                                            onChange={this.dicSubTypeChange.bind(this)} showClear={ false }/>
                                </FormItem>
                                {isField ?
                                    <FormItem>
                                        <Label>功能分类</Label>
                                        <Select dataSource={fieldType} name="fieldTypeId" placeholder="请选择功能分类" trigger={this.formFieldTypeTirgger.bind(this)} showClear={false} />
                                    </FormItem>
                                    :
                                    <FormItem style={{display:'none'}}>
                                        <Label>功能分类</Label>
                                        <Select dataSource={fieldType} name="fieldTypeId" placeholder="请选择功能分类" trigger={this.formFieldTypeTirgger.bind(this)} showClear={false} />
                                    </FormItem>
                                }
                                <FormItem disabled={true}>
                                    <Label>词库格式</Label>
                                    <Select placeholder="当前无词库格式" name="dicFormat" value={addFormat} showClear={ false }>
                                        <Option value='ordinary'>通用</Option>
                                        <Option value='map'>键值对</Option>
                                        <Option value='sentiment'>情感词</Option>
                                    </Select>
                                </FormItem>
                                <FormItem>
                                    <Label>词库标签</Label>
                                    <Select dataSource={addLabelType} name="functionId" placeholder="请选择词库标签" multiple={ true } search={ true }/>
                                </FormItem>
                                <FormItem>
                                    <Label>文件上传</Label>
                                    <Upload placeholder="上传文件" name="files" multiple={true} trigger={ this.formFileTirgger }/>
                                </FormItem>
                                { progressBool ? <Progress type="loading" /> : '' }
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeAddDicModal.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>

                    <Form type="inline"
                          method="post"
                          action={contextUrl + '/nlap/admin/dic/edit/tagged'}
                          trigger={ this.formEditTirgger.bind(this) }
                          async={true}
                          onAfterSubmit={this.afterSubmit}
                    >
                        <Modal visible={ updateDicVisible } size="middle" onClose={ this.closeUpdateDic.bind( this ) }>
                            <ModalHeader>
                                编辑词库
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{display:'none'}} name="dicId" value={dictId}/>
                                <Input style={{display:'none'}} name="tagIds" value={newLabelId}/>
                                <FormItem required={true} style={{width:'100%'}}>
                                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }} style={{padding:'0',left:'15px'}}><Label>词库名称</Label></Col>
                                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }}>
                                        <Input name="newDictName" placeholder="请输入词库名称" value={dicName} />
                                    </Col>
                                </FormItem>
                                <FormItem>
                                    <Col size={{ normal: 24, small: 4, medium: 24, large: 24 }} ><Label>词库标签</Label></Col>
                                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }} offset={ { small: 0, medium: 4, large: 4 } }>
                                        <Transfer
                                            style={{display:'inline-block'}}
                                            leftList={ unusedList }
                                            rightList={ usedList }
                                            render={ item => `${ item.text }` }
                                            titles={ ['未用标签', '已用标签'] }
                                            onChange={ this.ListChange.bind(this) }
                                            trigger={ this.triggerUpdateDic.bind( this ) }
                                        />
                                    </Col>


                                    {/*<Select dataSource={addLabelType} name="tagIds" placeholder="请选择词库标签"
                                     multiple={ true } search={ true } value={labelNameList}/>*/}
                                </FormItem>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeUpdateDic.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>

                    <Form type="inline"
                          method="post"
                          action={contextUrl + '/nlap/admin/tag/init/label/dicEnable'}
                          async={true}
                          onAfterSubmit={this.afterSubmit}
                    >
                        <Modal visible={ dicLabelVisible } size="middle" onClose={ this.closeEnabledModal.bind( this ) }>
                            <ModalHeader>
                                启用标签
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{display:'none'}} name="dicId" value={dicId}/>
                                <Input style={{display:'none'}} name="selectFunctionsId" value={selectLabelId}/>
                                <Transfer style={{left:'12%'}}
                                          leftList={ leftList }
                                          rightList={ rightList }
                                          render={ item => `${ item.text }` }
                                          titles={ ['停用标签', '启用标签'] }
                                          onChange={ this.handleListChange.bind(this) }
                                          trigger={ this.triggerEnabled.bind( this ) }
                                />

                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeEnabledModal.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">启用</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>

                    <Form type="horizontal"
                          method="post"
                          action={contextUrl + '/nlap/admin/dicInfo/word/upload'}
                          async={true}
                          enctype="multipart/form-data"
                          onSubmit={this.beforeUploadSubmit}
                          onAfterSubmit={this.afterAddSubmit}
                    >
                        <Modal visible={ uploadWordVisible } size="middle" onClose={ this.closeUploadWord.bind( this ) }>
                            <ModalHeader>
                                上传词
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{display:'none'}} name="dictId" value={dicId}/>
                                <FormItem>
                                    <Label>文件上传</Label>
                                    <Upload limit={1} placeholder={upload_format == 'map' ? '请上传键值对格式的文件' :
                                        (upload_format == 'ordinary' ? '请上传普通词格式的文件' : '请上传情感词格式的文件')}
                                            name="file" trigger={ this.formUploadTirgger }/>
                                </FormItem>
                                {progress_bool ? <Progress type="loading" /> : ''}
                                <Divider />
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{textAlign:'left'}}>
                                    <span>1、 系统支持.txt 格式的文本文件进行上传。<br/>
                                        2、 .txt 文件的
                                        {
                                            upload_format == 'map' ?
                                                <a href="/uniplatform/nlap/admin/file/downloadTemplate?fileName=map.txt">示例文件</a>
                                                :
                                                (upload_format == 'ordinary' ?
                                                        <a href="/uniplatform/nlap/admin/file/downloadTemplate?fileName=ordinary.txt">示例文件</a>
                                                        :
                                                        <a href="/uniplatform/nlap/admin/file/downloadTemplate?fileName=sentiment.txt">示例文件</a>
                                                )
                                        }
                                    </span>
                                </Col>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeUploadWord.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">上传</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>

                    <Form type="horizontal"
                          method="post"
                          action={contextUrl + '/nlap/admin/dicMgmt/dic/label'}
                          trigger={ this.formAddLabelTirgger.bind(this) }
                          async={true}
                          onSubmit={this.beforeAddLabelSubmit.bind(this)}
                          onAfterSubmit={this.afterAddLabelSubmit.bind(this)}
                    >
                        <Modal visible={ addLabelVisible } size="middle" onClose={ this.closeAddLabel.bind( this ) }>
                            <ModalHeader>
                                给词库打标签
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{display:'none'}} name="dicId" value={dictionaryId}/>
                                <FormItem>
                                    <Label>标签</Label>
                                    <Select dataSource={labelsList} value={labelsEchoList} name="labelIds" placeholder="请选择标签" multiple={ true } search={ true }/>
                                </FormItem>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeAddLabel.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确认</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>
                    <Form type="horizontal"
                          method="post"
                          action={contextUrl + '/nlap/admin/dicInfo/word/label'}
                          trigger={ this.formAddWordLabelTirgger.bind(this) }
                          async={true}
                          onSubmit={this.beforeAddWordLabelSubmit.bind(this)}
                          onAfterSubmit={this.afterAddWordLabelSubmit.bind(this)}
                    >
                        <Modal visible={ addWordLabelVisible } size="middle" onClose={ this.closeAddWordLabel.bind( this ) }>
                            <ModalHeader>
                                给词打标签
                            </ModalHeader>
                            <ModalBody>
                                <Input style={{display:'none'}} name="wordId" value={wordId}/>
                                <FormItem>
                                    <Label>标签</Label>
                                    <Select dataSource={labelsList} value={labelsWordEchoList} name="labelIds" placeholder="请选择标签" multiple={ true } search={ true }/>
                                </FormItem>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeAddWordLabel.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确认</Button>
                            </ModalFooter>
                        </Modal>
                    </Form>

                </Row>

                {/*<Footer />*/}
            </Page>
        );
    }

}
DictionaryManage.UIPage = page;
export default DictionaryManage;