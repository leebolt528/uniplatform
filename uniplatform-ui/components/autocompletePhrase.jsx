import React, { Component } from "react";
import { Page, Button, Input,Icon } from "epm-ui";

class InputAutoPhrase extends Component{
    constructor(props) {
        super( props );
        this.state = {
            dataSource:props.dataSource,
            valueArr:'',
            oneSplit:''
        }
    }
    componentDidMount(){
        this.getReady();
    }
    getReady(){
        //let inputNode=document.getElementById('o');//输入框
        let inputNode=document.getElementsByClassName('myInput')[0];
        let autoNode=document.getElementById('auto');//DIV的根节点
        let indexSelect = -1;//当前选中的DIV的索引
        let search_value = "";//保存当前搜索的字符
        this.setState({inputNode,autoNode,indexSelect,search_value});
    }
    //初始化DIV的位置
    init() {
        let {inputNode,autoNode}=this.state;
        autoNode.style.left = inputNode.offsetLeft + "px";
        autoNode.style.top = inputNode.offsetTop + inputNode.offsetHeight + "px";
        autoNode.style.width = inputNode.offsetWidth - 2 + "px"; //减去边框的长度2px  
    }
    //删除自动完成需要的所有DIV
    deleteDIV() {
        let {autoNode}=this.state;
        while (autoNode.hasChildNodes()) {
            autoNode.removeChild(autoNode.firstChild);
        }
        autoNode.className = "auto_hidden";
    }
    //设置值
    setValue(that,seq) {
        let {inputNode,autoNode,oneSplit}=this.state;
        return function() {
            autoNode.className = "auto_hidden";
            let reg = new RegExp("("+'^'+ `${oneSplit[oneSplit.length-1]}` + ")", "i");
            inputNode.value = seq;
            that.props.getter(inputNode.value);
        }
    }
    //响应键盘
    pressKey(event) {
        let {autoNode,indexSelect,inputNode,search_value}=this.state;
        let length = autoNode.children.length;
        //光标键"↓"
        if (event.keyCode == 40) {
            ++indexSelect;
            if (indexSelect > length) {
                indexSelect = 0;
            } else if (indexSelect == length) {
                inputNode.value = search_value;
            }
            this.changeClassname(length,indexSelect);
        }
        //光标键"↑"
        else if (event.keyCode == 38) {
            indexSelect--;
            if (indexSelect < -1) {
                indexSelect = length - 1;
            } else if (indexSelect == -1) {
                inputNode.value = search_value;
            }
            this.changeClassname(length,indexSelect);
        }
        //回车键
        else if (event.keyCode == 13) {
            autoNode.className = "auto_hidden";
            indexSelect = -1;
        } else {
            indexSelect = -1;
        }
        this.setState({indexSelect});
    }
    //更改classname
    changeClassname(length,indexSelect) {
         let {autoNode,inputNode,search_value,oneSplit}=this.state;
        for (let i = 0; i < length; i++) {
            if (i != indexSelect) {
                autoNode.childNodes[i].className = 'auto_onmouseout';
            } else {
                autoNode.childNodes[i].className = 'auto_onmouseover';  
                let reg = new RegExp("("+'^'+ `${oneSplit[oneSplit.length-1]}` + ")", "i");
                inputNode.value = autoNode.childNodes[i].seq;
            }
        }
        this.props.getter(inputNode.value);
    }
    //模拟鼠标移动至DIV时，DIV高亮
    autoOnmouseover(_div_index) {
        let {indexSelect,autoNode}=this.state;
        return function() {
            indexSelect= _div_index;
            let length = autoNode.children.length;
            for (let j = 0; j < length; j++) {
                if (j != indexSelect) {
                    autoNode.childNodes[j].className = 'auto_onmouseout';
                } else {
                    autoNode.childNodes[j].className = 'auto_onmouseover';
                }
            }
        }
    }
    autoOnmousedown(event){
        event.preventDefault();
    }
    autoBlur(){
        this.state.autoNode.className = "auto_hidden";
    }
    start(event) {
        let {search_value,inputNode,autoNode,oneSplit}=this.state;
        this.props.onKeyPress(event);
        if (event.which != 13 && event.which != 38 && event.which != 40) {
            this.init();
            this.deleteDIV();
            inputNode.value=inputNode.value.replace(/^\s+|\s+$/g,' ');
            search_value = inputNode.value;
            oneSplit=inputNode.value.replace(/^\s+|\s+$/g,'').split(/\s+/);
            this.setState({search_value,oneSplit});
            //初始按空格展示所有字段
            if(/^\s+$/.test(inputNode.value)||/(AND|OR|NOT)\s+$/.test(inputNode.value)){
                let valueArr = this.state.dataSource;
                if(valueArr.length>0){
                    valueArr.sort();
                }
                this.setState({valueArr},()=>this.createSelect(/[.]*/,false));
                return;
            }
             //初始匹配字段
            if(/^\s*[0-9a-zA-Z\_\$\u4e00-\u9fa5]+$/.test(inputNode.value)){
                inputNode.value=inputNode.value.replace(/(^\s*)/g, '');
                 if (inputNode.value.replace(/(^\s*)|(\s*$)/g, '') == "") {
                    return;
                } //值为空，退出
                let reg = new RegExp("("+'^'+ inputNode.value + ")","i");
                //let valueArr = '';
                let that=this;
                this.props.dataSource.then(function (valueArr) { 
                    if(valueArr.length>0){
                        valueArr.sort();
                        that.setState({valueArr},()=>that.createSelect(reg,true));
                    }
                 });
            }
        }
        this.pressKey(event);
        // window.onresize = Bind(this, function() {
        //     this.init();
        // });
    }
    //创建下拉列表
    createSelect(reg,bool){
        let {autoNode,valueArr}=this.state;
        let that=this;
        let div_index = 0; //记录创建的DIV的索引
        for (let i = 0; i < valueArr.length; i++) {
            //if (reg.test(valueArr[i])) {
                let div = document.createElement("div");
                div.className = "auto_onmouseout";
                div.seq = valueArr[i];
                div.onclick = this.setValue(that,valueArr[i]);
                div.onmouseover = this.autoOnmouseover(div_index);
                div.onmousedown = this.autoOnmousedown.bind(this);
                if(bool){
                    div.innerHTML = valueArr[i].replace(reg, "<strong>$1</strong>"); //搜索到的字符粗体显示
                }else{
                    div.innerHTML = valueArr[i];
                }
                autoNode.appendChild(div);
                autoNode.className = "auto_show";
                div_index++;
            //}
        }
    }
    //input失去焦点
    loseFocus(){
        this.state.autoNode.className = "auto_hidden";
    }
    changeValue(dataString){
        if(dataString.target.value!=''){
            document.getElementById('close').classList.add("close");
        }else{
            document.getElementById('close').classList.remove("close");
        }
        this.props.getter(dataString.target.value);
    }
    //autocomplete= off
    autoOff(){
       　if(navigator.userAgent.toLowerCase().indexOf("chrome") != -1){
    　　　　var selectors = document.getElementsByTagName("input");
    　　　　for(var i=0;i<selectors.length;i++){
    　　　　　　if((selectors[i].type !== "submit") && (selectors[i].type !== "password")){
    　　　　　　　　var input = selectors[i];
    　　　　　　　　var inputName = selectors[i].name;
    　　　　　　　　var inputid = selectors[i].id;
    　　　　　　　　selectors[i].removeAttribute("name");
    　　　　　　　　selectors[i].removeAttribute("id");
    　　　　　　　　setTimeout(function(){
    　　　　　　　　　　input.setAttribute("name",inputName);
    　　　　　　　　　　input.setAttribute("id",inputid);
    　　　　　　　　},1)
    　　　　　　}
    　　　　}
    　　}
    }
    clearInput(){
        this.state.inputNode.value='';
        document.getElementById('close').classList.remove("close");
        this.props.clickClear();
    }
    render(){
        let {value}=this.props;
        return(
            <div className="inputClose" style={{display:"inline-block",width:'70%',position:'relative'}}>
                <input type="text" className='myInput' id="o" style={{textAlign:'left'}} value={value} onKeyUp={this.start.bind(this)} 
                    onBlur={this.autoBlur.bind(this)}  onChange={this.changeValue.bind(this)} placeholder="请输入" autoComplete='off'/>
                <i className="fa fa-times-circle" id='close' onClick={this.clearInput.bind(this)}></i>
                <div className="auto_hidden" id="auto">
                </div>
            </div>
        )
    }
}

export default InputAutoPhrase;