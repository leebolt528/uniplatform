import React, { Component } from 'react';
import {Page, Row, Col, Menu} from 'epm-ui';

class UsouMenu extends Component {
    constructor( props ) {
        super( props );
        this.state = {}
        this.handleSelect = this.handleSelect.bind( this );
    }

    componentDidMount(){
        this.handleSelect();
    }

    handleSelect() {
        let link = this.props.url;
        let alink = document.getElementsByTagName('a');
        for(let i=0; i<alink.length; i++){
            if(alink[i].pathname == link){
                alink[i].parentNode.style.backgroundColor = '#f2f2f2';
            }
        }
        let flag1 = (link == '/usou/searchData_Browse') || (link == '/usou/searchData_Basic') ||
            (link == '/usou/searchData_Complex') || (link == '/usou/searchData_Sql');
        let flag2 = (link == '/usou/dicManage') || (link == '/usou/participleManage_Test');
        let flag3 = (link == '/usou/monitorManage_Result') || (link == '/usou/monitorManage_Rule');
        let flag4 = (link == '/usou/securityManage_Role') || (link == '/usou/securityManage_User');
        if(flag1){
            let submenu = document.getElementsByClassName('submenu');
            submenu[0].style.display = 'inline';
        }else if(flag2){
            let submenu = document.getElementsByClassName('submenu');
            submenu[1].style.display = 'inline';
        }else if(flag3){
            let submenu = document.getElementsByClassName('submenu');
            submenu[2].style.display = 'inline';
        }else if(flag4){
            let submenu = document.getElementsByClassName('submenu');
            submenu[3].style.display = 'inline';
        }
    }

    showSubMenu(index){
        let link = document.getElementsByClassName('link');
        let submenu = document.getElementsByClassName('submenu');
        for(let i=0; i<submenu.length; i++){
            if(index != i){
                submenu[i].style.display = 'none';
            }
        }
        if(submenu[index].style.display == 'none'){
            submenu[index].style.display = 'inline';
        }else {
            submenu[index].style.display = 'none';
        }
    }

    render() {
        return (
            <div className="account-l fl">
                <ul id="accordion" className="accordion">
                    <li>
                        <div className="link">
                            <img src="image/3.png" />
                            <a style={{textDecoration:'none',color:'#57585a',display:'block'}}
                               href={'/usou/clusterDetail?id='+this.props.id+'&name='+this.props.name}>集群详情</a>
                        </div>
                    </li>
                    <li>
                        <div className="link">
                            <img src="image/view thumbnail 2.png" />
                            <a style={{textDecoration:'none',color:'#57585a',display:'block'}}
                               href={'/usou/nodeList?id='+this.props.id+'&name='+this.props.name}>节点管理</a>
                        </div>
                    </li>
                    <li>
                        <div className="link"><img src="image/2.png" />
                            <a style={{textDecoration:'none',color:'#57585a',display:'block'}}
                               href={'/usou/indexManage?id='+this.props.id+'&name='+this.props.name}>索引管理</a>
                        </div>
                    </li>
                    <li>
                        <div className="link" onClick={this.showSubMenu.bind(this,0)}><img src="image/in12.png" />
                            <span style={{color:'#57585a'}}>数据查询</span>
                            <i className="fa fa-chevron-down"></i>
                        </div>
                        <ul className="submenu" style={{display:'none'}}>
                            <li><a href={'/usou/searchData_Browse?id='+this.props.id+'&name='+this.props.name}>浏览数据</a></li>
                            <li><a href={'/usou/searchData_Basic?id='+this.props.id+'&name='+this.props.name}>基本查询</a></li>
                            <li><a href={'/usou/searchData_Complex?id='+this.props.id+'&name='+this.props.name}>复合查询</a></li>
                            <li><a href={'/usou/searchData_Sql?id='+this.props.id+'&name='+this.props.name}>sql查询</a></li>
                        </ul>
                    </li>
                    <li>
                        <div className="link" onClick={this.showSubMenu.bind(this,1)}><img src="image/in1.png" />
                            <span style={{color:'#57585a'}}>分词管理</span>
                            <i className="fa fa-chevron-down"></i>
                        </div>
                        <ul className="submenu" style={{display:'none'}}>
                            <li><a href={'/usou/dicManage?id='+this.props.id+'&name='+this.props.name}>词典管理</a></li>
                            <li><a href={'/usou/participleManage_Test?id='+this.props.id+'&name='+this.props.name}>分词器测试</a></li>
                        </ul>
                    </li>
                    <li>
                        <div className="link" onClick={this.showSubMenu.bind(this,2)}><img src="image/5.png" />
                            <span style={{color:'#57585a'}}>集群监控管理</span>
                            <i className="fa fa-chevron-down"></i>
                        </div>
                        <ul className="submenu" style={{display:'none'}}>
                            <li><a href={'/usou/monitorManage_Result?id='+this.props.id+'&name='+this.props.name}>预警结果</a></li>
                            <li><a href={'/usou/monitorManage_Rule?id='+this.props.id+'&name='+this.props.name}>预警规则</a></li>
                        </ul>
                    </li>
                    <li>
                        <div className="link" onClick={this.showSubMenu.bind(this,3)}><img src="image/6.png" />
                            <span style={{color:'#57585a'}}>安全管理</span>
                            <i className="fa fa-chevron-down"></i>
                        </div>
                        <ul className="submenu" style={{display:'none'}}>
                            <li><a href={'/usou/securityManage_Role?id='+this.props.id+'&name='+this.props.name}>角色管理</a></li>
                            <li><a href={'/usou/securityManage_User?id='+this.props.id+'&name='+this.props.name}>用户管理</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        );
    }

}

export default UsouMenu;