<template>
  <div class="app-container">
    <div id="mapContainer"></div>
    <el-divider content-position="left">添加插件</el-divider>
    <div class="mt20" style="display: flex;justify-content: flex-start">
      <el-button link text type="primary" @click="addElasticMarker()">灵活点标记</el-button>
      <el-button link text type="primary" @click="addToolBar()">工具条</el-button>
      <el-button link text type="primary" @click="addScale()">比例尺</el-button>
      <el-button link text type="primary" @click="addHawkEye()">鹰眼</el-button>
      <el-button link text type="primary" @click="addControlBar()">组合控件</el-button>
      <el-button link text type="primary" @click="addMapType()">图层切换</el-button>
      <el-button link text type="primary" @click="addGeolocation()">浏览器定位</el-button>
      <el-button link text type="primary" @click="addAutoComplete()">输入提示</el-button>
      <el-button link text type="primary" @click="addPlaceSearch()">地点搜索</el-button>
      <el-button link text type="primary" @click="addDistrictSearch()">行政区查询</el-button>
      <el-button link text type="primary" @click="addLineSearch()">公交路线</el-button>
      <el-button link text type="primary" @click="addStationSearch()">公交站点</el-button>
      <el-button link text type="primary" @click="addDriving()">驾车路线</el-button>
      <el-button link text type="primary" @click="addTruckDriving()">货车路线</el-button>
      <el-button link text type="primary" @click="addTransfer()">公交换乘</el-button>
      <el-button link text type="primary" @click="addWalking()">步行路线</el-button>
    </div>

    <div class="mt10" style="display: flex;justify-content: flex-start">
      <el-button link text>鼠标工具绘制覆盖物</el-button>
      <el-button link text type="primary" @click="drawPolyline()" >绘制线段</el-button>
      <el-button link text type="primary" @click="drawPolygon()" >绘制多边形</el-button>
      <el-button link text type="primary" @click="drawRectangle()" >绘制矩形</el-button>
      <el-button link text type="primary" @click="drawCircle()" >绘制圆形</el-button>
      <el-button link text type="primary" @click="closeMouseTool()">关闭绘制</el-button>
    </div>
    <div class="mt10" style="display: flex;justify-content: flex-start">
      <el-button link text type="primary" @click="addRiding()">骑行路线</el-button>
      <el-button link text type="primary" @click="addDragRoute()">拖拽导航</el-button>
      <el-button link text type="primary" @click="addGeocoder()">地理编码</el-button>
      <el-button link text type="primary" @click="addCitySearch()">城市获取</el-button>
      <el-button link text type="primary" @click="addIndoorMap()">室内地图</el-button>
      <el-button link text type="primary" @click="addCircleEditor()">圆编辑</el-button>
      <el-button link text type="primary" @click="addPolygonEditor()">多边形编辑</el-button>
      <el-button link text type="primary" @click="addPolylineEditor()">折线编辑</el-button>
      <el-button link text type="primary" @click="addRectangleEditor()">矩形编辑</el-button>
      <el-button link text type="primary" @click="addEllipseEditor()">椭圆编辑</el-button>
      <el-button link text type="primary" @click="addBezierCurveEditor()">贝塞尔曲线编辑</el-button>
      <el-button link text type="primary" @click="addMarkerCluster()">点聚合</el-button>
      <el-button link text type="primary" @click="addRangingTool()">测距工具</el-button>
      <el-button link text type="primary" @click="addCloudDataSearch()">云图搜索</el-button>
      <el-button link text type="primary" @click="addWeather()">天气预报</el-button>
      <el-button link text type="primary" @click="addHeatMap()">热力图</el-button>
    </div>
  </div>
</template>

<script setup>
import {getCurrentInstance, onMounted, onUnmounted, reactive, ref,toRefs} from 'vue';
// 1.引入JS API Loader
import AMapLoader from '@amap/amap-jsapi-loader';

const {proxy} = getCurrentInstance();


// 2. 初始化地图实例
const map = ref();
// 3.异步加载插件对象
const plugins = reactive({
  elasticMarker: null,
  toolbar: null,
  scale: null,
  hawkEye: null,
  controlBar: null,
  mapType: null,
  geolocation: null,
  autoComplete: null,
  placeSearch: null,
  districtSearch: null,
  lineSearch: null,
  stationSearch: null,
  driving: null,
  truckDriving: null,
  transfer: null,
  walking: null,
  riding: null,
  dragRoute: null,
  geocoder: null,
  citySearch: null,
  indoorMap: null,
  mouseTool: null,
  circleEditor: null,
  polygonEditor: null,
  polylineEditor: null,
  rectangleEditor: null,
  ellipseEditor: null,
  bezierCurveEditor: null,
  markerCluster: null,
  rangingTool: null,
  cloudDataSearch: null,
  weather: null,
  heatMap: null,
})
// 1.灵活点标记
// 1.1 创建灵活点标记样式列表
const stylesArray = reactive([
  {
    icon: { //图标样式
      img: "https://a.amap.com/jsapi_demos/static/resource/img/men3.png",
      size: [16, 16], //图标的原始大小
      anchor: "bottom-center", //锚点位置
      fitZoom: 14, //最合适的级别 在此级别显示为图标原始大小
      scaleFactor: 2, //地图放大一级的缩放比例系数
      maxScale: 2, //图片的最大放大比例，随着地图放大图标会跟着放大，最大为2
      minScale: 1, //图片的最小缩小比例，随着地图缩小图标会跟着缩小，最小为1
    },
    label: { //文本标注
      content: "百花殿", //文本内容
      position: "BM", //文本位置相对于图标的基准点，"BM"为底部中央
      minZoom: 15, //label的最小显示级别，即文本标注在地图15级及以上，才会显示
    },
  },
  {
    icon: {
      img: "https://a.amap.com/jsapi_demos/static/resource/img/tingzi.png",
      size: [48, 63],
      anchor: "bottom-center",
      fitZoom: 17.5,
      scaleFactor: 2,
      maxScale: 2,
      minScale: 0.125,
    },
    label: {
      content: "万寿亭",
      position: "BM",
      minZoom: 15,
    },
  },
])
// 1.2 创建样式列表的级别映射
const zoomStyleMapping = ref({
  14: 0, //14-17级使用样式 0
  15: 0,
  16: 0,
  17: 0,
  18: 1, //18-20级使用样式 1
  19: 1,
  20: 1,
})
// 1.3 异步安装插件，加载灵活点标记
const addElasticMarker = () => {
  AMap.plugin("AMap.ElasticMarker", () => {
    plugins.elasticMarker = new AMap.ElasticMarker({
      position: [116.405562, 39.881166], // 点标记位置
      styles: stylesArray, // 指定样式列表
      zoomStyleMapping: zoomStyleMapping, //指定 zoom 与样式的映射
    });
    map.value.add(plugins.elasticMarker); // 添加到地图上
    map.value.setFitView(); // 缩放地图到合适的事业级别
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 2.地图控件
// 2.1 工具条/缩放控件
const addToolBar = () => {
  AMap.plugin("AMap.ToolBar", () => {
    plugins.toolbar = new AMap.ToolBar();
    map.value.addControl(plugins.toolbar);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 2.2 比例尺
const addScale = () => {
  AMap.plugin("AMap.Scale", () => {
    plugins.scale = new AMap.Scale();
    map.value.addControl(plugins.scale);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 2.3 鹰眼
const addHawkEye = () => {
  AMap.plugin("AMap.HawkEye", () => {
    plugins.hawkEye = new AMap.HawkEye();
    map.value.addControl(plugins.hawkEye);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 2.4 控制罗盘/组合控件
const addControlBar = () => {
  AMap.plugin("AMap.ControlBar", () => {
    plugins.controlBar = new AMap.ControlBar();
    map.value.addControl(plugins.controlBar);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 2.5 图层切换
const addMapType = () => {
  AMap.plugin("AMap.MapType", () => {
    plugins.mapType = new AMap.MapType();
    map.value.addControl(plugins.mapType);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 2.6 浏览器定位
const addGeolocation = () => {
  AMap.plugin("AMap.Geolocation", () => {
    plugins.geolocation = new AMap.Geolocation({
      enableHighAccuracy: true, // 是否使用高精度定位，默认：true
      timeout: 10000, // 设置定位超时时间，默认：无穷大
      offset: [10, 20],  // 定位按钮的停靠位置的偏移量
      zoomToAccuracy: true,  //  定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
      position: 'RB' //  定位按钮的排放位置,  RB表示右下
    });
    map.value.addControl(plugins.geolocation);
  })

  geoCurrentPosition()

}
// 获取当前定位信息
const geoCurrentPosition = () => {
  plugins.geolocation.getCurrentPosition((status, result) => {
    if (status === 'complete') {
      onComplete(result)
    } else {
      onError(result)
    }
  });

  function onComplete(data) {
    // data是具体的定位信息
    proxy.$modal.msgSuccess(`定位成功,当前位置为: ${data.position.lng},${data.position.lat}`)
  }

  function onError(data) {
    // 定位出错
    proxy.$modal.msgError(`定位失败，请检查是否开启定位权限`)
  }
}
// 输入提示
const addAutoComplete = () => {
  AMap.plugin("AMap.AutoComplete", () => {
    plugins.autoComplete = new AMap.AutoComplete({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 地点搜索
const addPlaceSearch = () => {
  AMap.plugin("AMap.PlaceSearch", () => {
    plugins.placeSearch = new AMap.PlaceSearch({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 行政区查询
const addDistrictSearch = () => {
  AMap.plugin("AMap.DistrictSearch", () => {
    plugins.districtSearch = new AMap.DistrictSearch({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 公交路线
const addLineSearch = () => {
  AMap.plugin("AMap.LineSearch", () => {
    plugins.lineSearch = new AMap.LineSearch({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 公交站点
const addStationSearch = () => {
  AMap.plugin("AMap.StationSearch", () => {
    plugins.stationSearch = new AMap.StationSearch({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 驾车路线
const addDriving = () => {
  AMap.plugin("AMap.Driving", () => {
    plugins.driving = new AMap.Driving({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 货车路线
const addTruckDriving = () => {
  AMap.plugin("AMap.TruckDriving", () => {
    plugins.truckDriving = new AMap.TruckDriving({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 公交换乘
const addTransfer = () => {
  AMap.plugin("AMap.Transfer", () => {
    plugins.transfer = new AMap.Transfer({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 步行路线
const addWalking = () => {
  AMap.plugin("AMap.Walking", () => {
    plugins.walking = new AMap.Walking({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 骑行路线
const addRiding = () => {
  AMap.plugin("AMap.Riding", () => {
    plugins.riding = new AMap.Riding({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 拖拽导航
const addDragRoute = () => {
  AMap.plugin("AMap.DragRoute", () => {
    plugins.dragRoute = new AMap.DragRoute(map.value, {
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 地理编码
const addGeocoder = () => {
  AMap.plugin("AMap.Geocoder", () => {
    plugins.geocoder = new AMap.Geocoder({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 城市获取
// 不需要精准地址可以定位当前城市
const addCitySearch = () => {
  proxy.$modal.msg("根据IP定位当前城市这里好像localhost不起效果，以后再看问题在哪")
  return
  AMap.plugin("AMap.CitySearch", () => {
    plugins.citySearch = new AMap.CitySearch();
    plugins.citySearch.getLocalCity((status, result) => {
      if (status === 'complete' && result.info === 'OK') {
        console.debug(result)
        proxy.$modal.msgSuccess(`当前城市为: ${result.city}`)
      }
    })
  })
}

// 室内地图
const addIndoorMap = () => {
  proxy.$modal.msg("室内地图这里我按照官网和AI都没搞出来，暂时没找到问题在哪，以后再改")
  return
  AMap.plugin("AMap.IndoorMap", () => {
    plugins.indoorMap = new AMap.IndoorMap();

    map.value.add(plugins.indoorMap)

    // plugins.indoorMap.showIndoorMap('B000A9VHIG')
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 绘制折线
const drawPolyline = () => {
  plugins.mouseTool.polyline({
    strokeColor: "#3366FF",
    strokeOpacity: 1,
    strokeWeight: 6,
    strokeStyle: "solid",
  })
}

// 绘制多边形
const drawPolygon = () => {
  plugins.mouseTool.polygon({
    strokeColor: "#FF33FF",
    strokeWeight: 6,
    strokeOpacity: 0.2,
    fillColor: '#1791fc',
    fillOpacity: 0.4,
    // 线样式还支持 'dashed'
    strokeStyle: "solid",
    // strokeStyle是dashed时有效
    // strokeDasharray: [30,10],
  })
}

// 绘制矩形
const drawRectangle = () => {
  plugins.mouseTool.rectangle({
    strokeColor: 'red',
    strokeOpacity: 0.5,
    strokeWeight: 6,
    fillColor: 'blue',
    fillOpacity: 0.5,
    strokeStyle: 'solid',
  })
}

// 绘制圆形
const drawCircle = () => {
  plugins.mouseTool.circle({
    strokeColor: "#FF33FF",
    strokeOpacity: 1,
    strokeWeight: 6,
    fillColor: '#1791fc',
    fillOpacity: 0.4,
    strokeStyle: 'solid',
  })
}

// 关闭绘制
const closeMouseTool = () => {
  if (!plugins.mouseTool) return
  plugins.mouseTool.close()
  proxy.$modal.msgSuccess('已关闭绘制')
}
// 圆编辑
const addCircleEditor = () => {
  AMap.plugin("AMap.CircleEditor", () => {
    plugins.circleEditor = new AMap.CircleEditor(map.value);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 多边形编辑
const addPolygonEditor = () => {
  AMap.plugin("AMap.PolygonEditor", () => {
    plugins.polygonEditor = new AMap.PolygonEditor(map.value);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 折线编辑
const addPolylineEditor = () => {
  AMap.plugin("AMap.PolylineEditor", () => {
    plugins.polylineEditor = new AMap.PolylineEditor(map.value);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 矩形编辑
const addRectangleEditor = () => {
  AMap.plugin("AMap.RectangleEditor", () => {
    plugins.rectangleEditor = new AMap.RectangleEditor(map.value);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 椭圆编辑
const addEllipseEditor = () => {
  AMap.plugin("AMap.EllipseEditor", () => {
    plugins.ellipseEditor = new AMap.EllipseEditor(map.value);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 贝塞尔曲线编辑
const addBezierCurveEditor = () => {
  AMap.plugin("AMap.BezierCurveEditor", () => {
    plugins.bezierCurveEditor = new AMap.BezierCurveEditor(map.value);
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 点聚合
const addMarkerCluster = () => {
  AMap.plugin("AMap.MarkerCluster", () => {
    plugins.markerCluster = new AMap.MarkerCluster(map.value, [], {
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 测距工具
const addRangingTool = () => {
  AMap.plugin("AMap.RangingTool", () => {
    plugins.rangingTool = new AMap.RangingTool(map.value, {
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 云图搜索
const addCloudDataSearch = () => {
  AMap.plugin("AMap.CloudDataSearch", () => {
    plugins.cloudDataSearch = new AMap.CloudDataSearch({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 天气预报
const addWeather = () => {
  AMap.plugin("AMap.Weather", () => {
    plugins.weather = new AMap.Weather({
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}

// 热力图
const addHeatMap = () => {
  AMap.plugin("AMap.HeatMap", () => {
    plugins.heatMap = new AMap.HeatMap(map.value, {
      // 配置参数
    });
  })
  proxy.$modal.msgSuccess('添加成功')
}
const initMap = () => {

  AMapLoader.load({
    "key": "518e0f027c8f3814e9f84505b879ed5b",              // 申请好的Web端开发者Key，首次调用 load 时必填
    "version": "2.0",       // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
    "plugins": ["AMap.IndoorMap"],
    "Loca": {               // 是否加载 Loca， 缺省不加载
      "version": '2.0.0'// Loca 版本，缺省 1.3.2
    },
  }).then((AMap) => {
    // 初始化地图
    var indoorMap = new AMap.IndoorMap();
    map.value = new AMap.Map('mapContainer', {
      viewMode: '3D',  // 地图设置成 3D 模式，否则图层会失去高度信息
      zoom: 18,
      center: [116.518542, 39.924677],
      showIndoorMap: true,
      layers: [indoorMap,AMap.createDefaultLayer()]
    });
    indoorMap.showIndoorMap('B000A9VHIG')

    // 添加鼠标工具
    AMap.plugin("AMap.MouseTool", () => {
      plugins.mouseTool = new AMap.MouseTool(map.value);
      plugins.mouseTool.on("draw",(event) => {
        //event.obj 为绘制出来的覆盖物对象
        console.log("覆盖物对象绘制完成");
        console.debug(event.obj)
      })
    })
  }).catch(e => {
    console.error(e);
  });
};


onMounted(() => {
  // 确保 DOM 已经挂载后再初始化地图
  initMap();
});

onUnmounted(() => {
  if (map) {
    map.value?.destroy();
  }
});


</script>

<style scoped lang="scss">
#mapContainer {
  width: 100%;
  height: 600px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>
