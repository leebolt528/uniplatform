import React, { Component } from 'react';
import { Row, Col, Text} from 'epm-ui';

class NodeChart extends Component {

  constructor(props) {
    super(props);

    this.state = {
        chart: null,
        info: null
    }

  }

  // 实例化图表
  componentDidMount() {
    let name = this.props.id;

    this.setState({
      chart: bigdesk_charts[name].chart(d3.select('#svg_'+ name +'').attr('clip_id', 'clip_'+ name +'')),
      info: templates[name]
    });

  }

  // 定时更新数据
  componentWillUpdate() {
    let chart = this.state.chart;

    let name = this.props.id;
    let stats = this.props.stats;
    let number = this.props.number;
    let dataExist = this.props.dataExist;

    if (stats.length > 0) {


      if (dataExist) {
        // 更新图表下面的描述信息
        bigdesk_charts[name].describe(stats[stats.length-1], dataExist);

        // 存在数据
        let data1 = bigdesk_charts[name].series1(stats);
        let data2 = bigdesk_charts[name].series2(stats);

        try {

          if (+number == 3) {
            let data3 = bigdesk_charts[name].series3(stats);
            chart.animate(true).update(data1, data2, data3);
          }else {
            chart.animate(true).update(data1, data2);
          }

        } catch (ignore) {

        }

      }else if (typeof(dataExist) != "undefined"){
        // 没数据
        chart = bigdesk_charts.not_available.chart(chart.svg());

        // 更新图表下面的描述信息
        bigdesk_charts[name].describe(stats[stats.length-1], dataExist);
      }

    }

  }

  render() {
    return (
      <div>
        <svg id={'svg_' + this.props.id} preserveAspectRatio="xMinYMid" viewBox="0 0 270 160" ></svg>
        <div dangerouslySetInnerHTML={{ __html: this.state.info}} >
        </div>
      </div>
    );

  }

}

export default NodeChart;
