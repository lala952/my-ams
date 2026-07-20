<template>
  <div class="app-container">
    <div id="mapContainer" style="width: 100%; height:600px;"/>
    <div class="mt20" style="display: flex;justify-content: space-between">
      <div>
        <el-button link text>地图级别:</el-button>
        <el-button link text type="primary" @click="handleZoomTo(18)">18</el-button>
        <el-button link text type="primary" @click="handleZoomTo(15)">15</el-button>
        <el-button link text type="primary" @click="handleZoomTo(14)">14</el-button>
        <el-button link text type="primary" @click="handleZoomTo(12)">12</el-button>
        <el-button link text type="primary" @click="handleZoomTo(10)">10</el-button>
        <el-button link text type="primary" @click="handleZoomTo(8)">8</el-button>
        <el-divider direction="vertical"/>
        <el-button link text @click="getMapBounds">地图样式:</el-button>
        <el-button link text type="primary" @click="setMapStyle('black')">暗黑</el-button>
        <el-button link text type="primary" @click="setMapStyle('indigo')">靛蓝</el-button>
        <el-button link text type="primary" @click="resetMapStyle()">默认</el-button>
      </div>
      <div>
        <el-button link text type="default" icon="ZoomIn" @click="handleZoomIn()">放大</el-button>
        <el-button link text type="default" icon="ZoomOut" @click="handleZoomOut">缩小</el-button>
      </div>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>获取地图信息:</el-button>
      <el-button link text type="primary" @click="getMapCenter">地图中心点坐标</el-button>
      <el-button link text type="primary" @click="getMapBounds">地图范围</el-button>
      <el-button link text type="primary" @click="getMapZoom">地图缩放级别</el-button>
      <el-dialog v-model="openMapBounds" :title="title" append-to-body>
        <p>当前地图可视范围是: <span class="link-type">{{ bssw?.lng }},{{ bssw?.lat }}</span> 到 <span
            class="link-type">{{ bsne?.lng }},{{ bsne?.lat }}</span></p>
        <template #footer>
          <el-button @click="openMapBounds = false">关闭</el-button>
        </template>
      </el-dialog>
      <el-dialog v-model="openMapCenter" :title="title" append-to-body>
        <p>当前地图中心点坐标是: <span class="link-type">{{ mapCenter?.lng }},{{ mapCenter?.lat }}</span></p>
        <template #footer>
          <el-button @click="openMapCenter = false">关闭</el-button>
        </template>
      </el-dialog>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text @click="handleMapCenterAndZoom">设置地图中心点及缩放级别</el-button>
      <el-dialog v-model="openMapCenterAndZoom" :title="title" append-to-body>
        <el-form :inline="true" label-width="120">
          <el-row :gutter="10">
            <el-col :span="8">
              <el-form-item label="经度" style="width: 100%">
                <el-input v-model="lng" placeholder="请输入经度"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="纬度" style="width: 100%">
                <el-input v-model="lat" placeholder="请输入纬度"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="缩放级别" style="width: 100%">
                <el-input-number :step="1" :min="1" :max="18" v-model="zoom" placeholder="请输入缩放级别"/>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <template #footer>
          <el-button @click="openMapCenterAndZoom = false">关闭</el-button>
          <el-button type="primary" @click="setMapCenterAndZoom()">设置</el-button>
        </template>
      </el-dialog>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>添加控件</el-button>
      <el-button link text type="primary" @click="addZoomControl()">缩放控件</el-button>
      <el-button link text type="primary" @click="addScaleControl()">比例尺</el-button>
      <el-button link text type="primary" @click="addOverviewControl()">鹰眼</el-button>
      <el-button link text type="primary" @click="addCopyrightControl()">版权</el-button>
      <el-button link text type="primary" @click="addmilitaryControl()">标绘</el-button>
      <el-button link text type="primary" @click="addMapTypeControl()">地图类型</el-button>
      <el-button link text type="primary" @click="removeAllControls()">移除所有控件</el-button>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>添加覆盖物</el-button>
      <el-button link text type="primary" @click="add">添加标注</el-button>
      <el-button link text type="primary" @click="add2">添加自定义标注图片</el-button>
      <el-button link text type="primary" @click="add3">添加文字标注</el-button>
      <el-button link text type="primary" @click="add4">添加线</el-button>
      <el-button link text type="primary" @click="add5">添加多边形</el-button>
      <el-button link text type="primary" @click="add6">添加带洞多边形</el-button>
      <el-button link text type="primary" @click="add7">添加矩形</el-button>
      <el-button link text type="primary" @click="add8">添加圆</el-button>
      <el-button link text type="primary" @click="add9">添加信息窗口</el-button>
      <el-button link text type="primary" @click="add10">添加用户自定义信息窗口</el-button>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>添加覆盖物</el-button>
      <el-button link text type="primary" @click="add11">编辑线-启动编辑</el-button>
      <el-button link text type="primary" @click="add12">编辑线-禁止编辑</el-button>
      <el-button link text type="primary" @click="add13">编辑面条-启动编辑</el-button>
      <el-button link text type="primary" @click="add14">编辑面条-禁止编辑</el-button>
      <el-button link text type="primary" @click="add15">清除覆盖物</el-button>
      <el-button link text type="primary" @click="add16">点拖拽</el-button>
      <el-button link text type="primary" @click="add17">点不可拖拽</el-button>
      <el-button link text type="primary" @click="add18">覆盖物显示</el-button>
      <el-button link text type="primary" @click="add19">覆盖物隐藏</el-button>
      <el-button link text type="primary" @click="add20">添加覆盖物的文字标签</el-button>
      <el-button link text type="primary" @click="add21">添加多个点的标注</el-button>
      <el-button link text type="primary" @click="add22">图片覆盖物类</el-button>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>标注工具</el-button>
      <el-button link text type="primary" @click="openMarkerTool()">开启标注</el-button>
      <el-button link text type="primary" @click="closeMarkerTool()">关闭标注</el-button>
      <el-button link text type="primary" @click="editMarkers()">编辑标注</el-button>
      <el-button link text type="primary" @click="clearAllMarkers()">清除标注</el-button>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>矩形工具</el-button>
      <el-button link text type="primary" @click="openRectangleTool()">绘制矩形</el-button>
      <el-button link text type="primary" @click="closeRectangleTool()">结束绘制</el-button>
      <el-button link text type="primary" @click="editRectangles()">编辑矩形</el-button>
      <el-button link text type="primary" @click="clearAllRectangles()">清除矩形</el-button>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>测距工具</el-button>
      <el-button link text type="primary" @click="openLineTool()">开启</el-button>
      <el-button link text type="primary" @click="closeLineTool()">关闭</el-button>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>测面工具</el-button>
      <el-button link text type="primary" @click="openPolygonTool()">开启</el-button>
      <el-button link text type="primary" @click="closePolygonTool()">关闭</el-button>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>画圆工具</el-button>
      <el-button link text type="primary" @click="openCircleTool()">开启</el-button>
      <el-button link text type="primary" @click="closeCircleTool()">关闭</el-button>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>地图组件</el-button>
      <el-button link text type="primary" @click="startPicker()">启动地图拾取器</el-button>
      <el-button link text type="primary" @click="addLineGrid()">添加格网图层</el-button>
      <el-button link text type="primary" @click="removeLineGrid()">移除格网图层</el-button>
      <el-button link text type="primary" @click="addPaintBrush()">添加画笔工具</el-button>
      <el-button link text type="primary" @click="closePaintBrush()">关闭画笔工具</el-button>
      <el-button link text type="primary" @click="clearDraw()">清除绘制</el-button>
    </div>
    <div class="mt10" style="display: flex;">
      <el-button link text>用鼠标在地图上选取获得的地理坐标为：<span class="link-type">{{ position }}</span></el-button>
    </div>
    <div class="mt10">
      <el-button link text>平移</el-button>
      <el-button link text type="primary" @click="panTo(beijing.longitude, beijing.latitude)">
        北京
      </el-button>
      <el-button link text type="primary" @click="panTo(beijingshunyi.longitude, beijingshunyi.latitude)">
        北京顺义
      </el-button>
      <el-button link text type="primary"
                 @click="panTo(chengduwangfujin.longitude, chengduwangfujin.latitude)">
        成都王府井B座
      </el-button>
      <el-button link text type="primary" @click="panTo(linyang.longitude, linyang.latitude)">
        古蔺县蔺阳中学
      </el-button>
    </div>
    <div class="mt10">
      <el-button link text>定位</el-button>
      <el-button link text type="primary" @click="geoLocalCity()">
        IP城市定位
      </el-button>
      <el-button link text type="primary" @click="geoLocation()">
        H5
      </el-button>
    </div>
  </div>
</template>

<script setup lang="js">
import {getCurrentInstance, onMounted, onUnmounted, reactive, ref, toRefs} from 'vue';

const {proxy} = getCurrentInstance();
// 地图实例
const map = ref(null);
const zoom = ref(15);
const mapCenter = ref(new T.LngLat(116.391250, 39.908120))
const menu = ref(null)
const txtMenuItem = ref([
  {
    text: '放大',
    callback: function() {
      map.value.zoomIn()
    }
  },
  {
    text: '缩小',
    callback: function() {
      map.value.zoomOut()
    }
  },
  {
    text: '放大至最大级',
    callback: function() {
      map.value.setZoom(18)
    }
  },
  {
    text: '查看全国',
    callback: function() {
      map.value.setZoom(4)
    }
  },{
    text: '获得右键点击坐标',
    isDisable: false,
    callback: function(lnglat) {
      proxy.$modal.msgSuccess(`${lnglat.getLng()}，${lnglat.getLat()}`)
    }
  },
])

// 自定义图层对象
const imageLayer = ref(null)
const imageURL = "http://t0.tianditu.gov.cn/img_w/wmts?"
    + "SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles"
    + "&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=66a6de480dbec1a0b2393cd1f643096d";

// 地点数据
const data = reactive({
  tk: '66a6de480dbec1a0b2393cd1f643096d',
  layerType: 'terrain',
  locations: [
    {name: '北京', longitude: 116.39125, latitude: 39.90713},
    {name: '北京顺义', longitude: 116.661474, latitude: 40.149891},
    {name: '成都王府井B座', longitude: 104.0774, latitude: 30.66136},
    {name: '古蔺县蔺阳中学', longitude: 105.81073, latitude: 28.03978}
  ]
})
const locations = toRefs(data.locations);
const [beijing, beijingshunyi, chengduwangfujin, linyang] = locations;

// 控件管理
const controls = reactive({
  zoom: null,
  scale: null,
  overview: null,
  copyright: null,
  military: null,
  mapType: null
})

// 覆盖物
const marker = ref(null)
const line = ref(null)
const polygon = ref(null)

// 标注工具
const markerTool = ref(null)
const markers = ref([])
const editingMode = ref(false)

// 矩形工具
const rectTool = ref(null)
const rectangles = ref([])
const editingRectMode = ref(false)

// 测距/测面工具
const lineTool = ref(null)
const polygonTool = ref(null)

// 画圆工具
const circleTool = ref(null)

// 地图组件
const coordinatePicker = ref(null)
const position = ref(null)
const glayer = ref(null)
const handler = ref(null)
const drawings = ref([])

// 弹窗相关
const title = ref()
const openMapBounds = ref(false)
const openMapCenter = ref(false)
const openMapCenterAndZoom = ref(false)
const bssw = ref([])
const bsne = ref([])
const lng = ref(mapCenter.value.lng)
const lat = ref(mapCenter.value.lat)


//  初始化地图
function initMap() {
  const T = window.T;

  // 自定义图层对象
  imageLayer.value = new T.TileLayer(imageURL, {
    minZoom: 1,
    maxZoom: 10
  })

  const config = {
    layers: [imageLayer.value]
  }
  // 将图层添加到地图上,方式一：直接在地图初始化后将这行代码复制到map.value.centerAndZoom(mapCenter.value, zoom.value);之后；
  // 方式二：配置一个config，在地图初始化时传递,map.value = new T.Map("mapContainer",config);
  // 但是如果是这样配置的话，默认就是卫星地图，但我想的是尽量先使用矢量地图，卫星地图等用户自行添加控件来选择，所以我目前代码实现的的方式一


  map.value = new T.Map("mapContainer");
  // map.value = new T.Map("mapContainer",config);
  map.value.centerAndZoom(mapCenter.value, zoom.value);

  map.value.addLayer(imageLayer.value)

  // 创建右键菜单对象
  menu.value = new T.ContextMenu({width: 140});
  // 添加右键菜单
  for (let i = 0; i < txtMenuItem.value.length; i++) {
    // 添加菜单项
    let item = new T.MenuItem(txtMenuItem.value[i].text,txtMenuItem.value[i].callback);
    menu.value.addItem(item);
    // 添加分割线
    menu.value.addSeparator();
  }
  map.value.addContextMenu(menu.value);

  // 矩形工具
  rectTool.value = new T.RectangleTool(map.value);
  rectTool.value.addEventListener("draw", handleRectangleDrawn);

  // 画圆工具
  circleTool.value = new T.CircleTool(map.value, {
    color: "blue", weight: 3, opacity: 0.5, fillColor: "#FFFFFF", fillOpacity: 0.5
  });
  circleTool.value.on('draw', (e) => {
    onDrawCircle(e.currentCenter, e.currentRadius);
  });

  // 测距和测面工具
  const toolConfig = {showLabel: true};
  lineTool.value = new T.PolylineTool(map.value, toolConfig);
  polygonTool.value = new T.PolygonTool(map.value, toolConfig);

  // 标注工具
  markerTool.value = new T.MarkTool(map.value, {follow: true});
  markerTool.value.addEventListener("mark", handleMarkerAdded);

  // 画笔工具
  handler.value = new T.PaintBrushTool(map.value, {
    keepdrawing: true,
    style: {
      weight: 3
    }
  });
}

//  地图基本操作

// 放大缩小
function handleZoomIn() {
  map.value.zoomIn()
}

function handleZoomOut() {
  map.value.zoomOut()
}

// 缩放地图
function handleZoomTo(level) {
  map.value.setZoom(level)
}

// 平移
function panTo(longitude, latitude) {
  map.value.panTo(new T.LngLat(longitude, latitude))
}

//  地图样式
function setMapStyle(style) {
  map.value.setStyle(style)
  proxy.$modal.msgSuccess("设置成功")
}

function resetMapStyle() {
  map.value.removeStyle()
  proxy.$modal.msgSuccess("设置成功")
}

//  获取地图信息

function getMapBounds() {
  let bs = map.value.getBounds();
  bssw.value = bs.kq;
  bsne.value = bs.Lq;
  openMapBounds.value = true
  title.value = '当前地图可视范围'
}

function getMapCenter() {
  title.value = '当前地图中心点坐标'
  console.debug(map.value.getCenter())
  mapCenter.value = map.value.getCenter();
  openMapCenter.value = true
}

function getMapZoom() {
  proxy.$modal.msgSuccess(`当前地图缩放级别是:${map.value.getZoom()}`)
}

//  设置地图中心点及缩放级别

function handleMapCenterAndZoom() {
  title.value = '设置当前地图中心点坐标和缩放级别'
  openMapCenterAndZoom.value = true
}

function setMapCenterAndZoom() {
  map.value.centerAndZoom(new T.LngLat(lng.value, lat.value), zoom.value)
  openMapCenterAndZoom.value = false
  proxy.$modal.msgSuccess("设置成功")
}

//  添加标记函数

function addMarker(lnglat, title) {
  const T = window.T;
  const marker = new T.Marker(lnglat);
  map.value.addOverLay(marker);
}

//  控件管理

function addZoomControl() {
  if (map.value && controls.zoom) {
    proxy.$modal.msgWarning("已存在，无需添加")
    return
  }
  const control = new T.Control.Zoom();
  control.setPosition(T_ANCHOR_TOP_LEFT);
  map.value.addControl(control);
  controls.zoom = control
  proxy.$modal.msgSuccess("添加成功")
}

function addScaleControl() {
  if (map.value && controls.scale) {
    proxy.$modal.msgWarning("已存在，无需添加")
    return
  }
  const control = new T.Control.Scale();
  control.setPosition(T_ANCHOR_BOTTOM_LEFT);
  map.value.addControl(control);
  controls.scale = control
  proxy.$modal.msgSuccess("添加成功")
}

function addOverviewControl() {
  if (map.value && controls.overview) {
    proxy.$modal.msgWarning("已存在，无需添加")
    return
  }
  const control = new T.Control.OverviewMap({
    isOpen: true,
    size: new T.Point(150, 150)
  });
  map.value.addControl(control);
  controls.overview = control
  proxy.$modal.msgSuccess("添加成功")
}

function addCopyrightControl() {
  if (map.value && controls.copyright) {
    proxy.$modal.msgWarning("已存在，无需添加")
    return
  }
  const control = new T.Control.Copyright({
    position: T_ANCHOR_TOP_LEFT
  });
  map.value.addControl(control);
  const bs = map.value.getBounds()
  control.addCopyright(
      {
        id: 1,
        content: "<a href='https://www.tianditu.gov.cn' target='_blank' style='fon\n" +
            "t-size:18px;color:#fff'>© 资产管理系统 2026 </a>",
        bounds: bs
      }
  )
  controls.copyright = control
  proxy.$modal.msgSuccess("添加成功")
}

function addmilitaryControl() {
  if (map.value && controls.military) {
    proxy.$modal.msgWarning("已存在，无需添加")
    return
  }
  const control = new T.Control.militarySymbols({position: T_ANCHOR_TOP_LEFT});
  map.value.addControl(control);
  controls.military = control
  proxy.$modal.msgSuccess("添加成功")
}

function addMapTypeControl() {
  if (map.value && controls.mapType) {
    proxy.$modal.msgWarning("已存在，无需添加")
    return
  }
  const control = new T.Control.MapType();
  map.value.addControl(control);
  controls.mapType = control
  proxy.$modal.msgSuccess("添加成功")
}

function removeAllControls() {
  if (!map.value) {
    proxy.$modal.msgWarning('地图实例不存在')
    return
  }

  let removedCount = 0

  if (controls.zoom) {
    map.value.removeControl(controls.zoom)
    controls.zoom = null
    removedCount++
  }
  if (controls.scale) {
    map.value.removeControl(controls.scale)
    controls.scale = null
    removedCount++
  }
  if (controls.overview) {
    map.value.removeControl(controls.overview)
    controls.overview = null
    removedCount++
  }
  if (controls.copyright) {
    map.value.removeControl(controls.copyright)
    controls.copyright = null
    removedCount++
  }
  if (controls.military) {
    map.value.removeControl(controls.military)
    controls.military = null
    removedCount++
  }
  if (controls.mapType) {
    map.value.removeControl(controls.mapType)
    controls.mapType = null
    removedCount++
  }

  if (removedCount > 0) {
    proxy.$modal.msgSuccess(`已移除 ${removedCount} 个控件`)
  } else {
    proxy.$modal.msgWarning('没有可移除的控件')
  }
}


//  添加覆盖物

// 添加标注
const add = () => {
  marker.value = new T.Marker(new T.LngLat(116.411794, 39.9068))
  map.value.addOverLay(marker.value)
}

// 添加自定义标注图片
const add2 = () => {
  const icon = new T.Icon({
    iconUrl: "http://api.tianditu.gov.cn/img/map/markerA.png",
    iconSize: new T.Point(19, 27),
    iconAnchor: new T.Point(10, 25)
  })
  const marker2 = new T.Marker(new T.LngLat(116.411794, 39.9068), {icon: icon})
  map.value.addOverLay(marker2)
  proxy.$modal.msgSuccess("成功添加自定义标注")
}

// 添加文字标注
const add3 = () => {
  const latlng = new T.LngLat(116.420837, 39.902395)
  const label = new T.Label({
    text: "天地图官网网址：<a href='https://www.tianditu.gov.cn'  target='_blank'>https://www.tianditu.gov.cn </a>",
    position: latlng,
    offset: new T.Point(-9, 0)
  })
  map.value.addOverLay(label)
  proxy.$modal.msgSuccess("成功添加文字标注")
}

// 添加线
const add4 = () => {
  let points = []
  points.push(new T.LngLat(116.41136, 39.97569))
  points.push(new T.LngLat(116.411794, 39.9068))
  points.push(new T.LngLat(116.32969, 39.92940))
  points.push(new T.LngLat(116.385438, 39.90610))
  line.value = new T.Polyline(points)
  map.value.addOverLay(line.value)
  proxy.$modal.msgSuccess("成功添加线")
}

// 添加多边形
const add5 = () => {
  const points = []
  points.push(new T.LngLat(116.41136, 39.97569))
  points.push(new T.LngLat(116.411794, 39.9068))
  points.push(new T.LngLat(116.32969, 39.92940))
  points.push(new T.LngLat(116.385438, 39.90610))
  polygon.value = new T.Polygon(points, {
    color: "blue", weight: 3, opacity: 0.5, fillColor: "#FFFFFF", fillOpacity: 0.5
  })
  map.value.addOverLay(polygon.value)
  proxy.$modal.msgSuccess("成功添加多边形")
}

// 添加带洞多边形
const add6 = () => {
  const points = []
  points.push(new T.LngLat(116.315000, 39.964750))
  points.push(new T.LngLat(116.303330, 39.960810))
  points.push(new T.LngLat(116.306760, 39.866270))
  points.push(new T.LngLat(116.328740, 39.847560))
  points.push(new T.LngLat(116.366500, 39.855730))
  points.push(new T.LngLat(116.442380, 39.856520))
  points.push(new T.LngLat(116.455080, 39.865480))
  points.push(new T.LngLat(116.456110, 39.950020))
  points.push(new T.LngLat(116.428990, 39.967390))

  const points1 = []
  points1.push(new T.LngLat(116.349330, 39.941590))
  points1.push(new T.LngLat(116.350620, 39.900650))
  points1.push(new T.LngLat(116.353110, 39.898670))
  points1.push(new T.LngLat(116.413880, 39.899990))
  points1.push(new T.LngLat(116.418340, 39.903150))
  points1.push(new T.LngLat(116.429330, 39.903940))
  points1.push(new T.LngLat(116.427270, 39.947910))
  points1.push(new T.LngLat(116.365810, 39.946600))
  polygon.value = new T.Polygon([points, points1], {
    color: "black", weight: 3, opacity: 0.2, fillColor: "purple", fillOpacity: 0.5
  })
  map.value.addOverLay(polygon.value)
  proxy.$modal.msgSuccess("成功添加带洞多边形")
}

// 添加矩形
const add7 = () => {
  const bounds = new T.LngLatBounds(
      new T.LngLat(116.36231, 39.87784),
      new T.LngLat(116.43954, 39.92841)
  )
  const rect = new T.Rectangle(bounds)
  rect.setStyle({
    color: "#FF00FF",
    weight: 3,
    fillColor: "#FFFF99",
    fillOpacity: 0.5
  })
  map.value.addOverLay(rect)
  proxy.$modal.msgSuccess("成功添加矩形")
}

// 添加圆
const add8 = () => {
  const circle = new T.Circle(
      new T.LngLat(116.40093, 39.90313),
      3000,
      {
        color: "purple",
        weight: 5,
        opacity: 0.5,
        fillColor: "#FFFFFF",
        fillOpacity: 0.5,
        lineStyle: "solid"
      }
  )
  map.value.addOverLay(circle)
  proxy.$modal.msgSuccess("成功添加圆")
}

// 添加信息窗口
const add9 = () => {
  const lnglat = new T.LngLat(116.40969, 39.94940)
  const infoWin = new T.InfoWindow()
  infoWin.setLngLat(lnglat)
  infoWin.setContent("测试：要添加的信息窗口666")
  map.value.addOverLay(infoWin)
  proxy.$modal.msgSuccess("成功添加信息窗口")
}

// 添加用户自定义信息窗口
const add10 = () => {
  const lnglat = new T.LngLat(116.40969, 39.94940)
  const infoWin = new T.InfoWindow()
  infoWin.setLngLat(lnglat)
  let content = "<div style='color: #42b983'><h3>hello world</h3><input></div>"
  infoWin.setContent(content)
  map.value.addOverLay(infoWin)
  proxy.$modal.msgSuccess("成功添加用户自定义信息窗口")
}

// 编辑线-启动编辑
const add11 = () => {
  if (!line.value) {
    proxy.$modal.msgError("请先添加线")
  }
  line.value.enableEdit()
  proxy.$modal.msgSuccess("已启动编辑")
}

// 编辑线-禁止编辑
const add12 = () => {
  line.value.disableEdit()
  proxy.$modal.msgSuccess("已禁止编辑")
}

// 编辑面条-启动编辑
const add13 = () => {
  if (polygon.value) {
    polygon.value.enableEdit()
  }
  proxy.$modal.msgSuccess("已启动编辑")
}

// 编辑面条-禁止编辑
const add14 = () => {
  if (polygon.value) {
    polygon.value.disableEdit()
  }
  proxy.$modal.msgSuccess("已禁止编辑")
}

// 清除覆盖物
const add15 = () => {
  if (map.value) {
    map.value.clearOverLays()
  }
  proxy.$modal.msgSuccess("已清除覆盖物")
}

// 点拖拽
const add16 = () => {
  if (marker.value) {
    marker.value.enableDragging()
  }
  proxy.$modal.msgSuccess("已启动拖拽")
}

// 点不可拖拽
const add17 = () => {
  if (marker.value) {
    marker.value.disableDragging()
  }
  proxy.$modal.msgSuccess("已禁止拖拽")
}

// 覆盖物显示
const add18 = () => {
  if (polygon.value) {
    polygon.value.show()
  }
  proxy.$modal.msgSuccess("已显示覆盖物")
}

// 覆盖物隐藏
const add19 = () => {
  if (polygon.value) {
    polygon.value.hide()
  }
  proxy.$modal.msgSuccess("已隐藏覆盖")
}

// 添加覆盖物的文字标签
const add20 = () => {
  const zoom = 12
  map.value.centerAndZoom(new T.LngLat(116.40093, 39.90313), zoom)
  const marker2 = new T.Marker(new T.LngLat(116.400244, 39.92556))
  map.value.addOverLay(marker2)
  const label = new T.Label({
    text: "<b style='color:var(--el-text-color-primary)'>文字标注!!!<b>",
    position: marker2.getLngLat(),
    offset: new T.Point(3, -30)
  })
  map.value.addOverLay(label)
  marker2.addEventListener("drag", function (e) {
    label.setLngLat(marker2.getLngLat())
  })
  proxy.$modal.msgSuccess("成功添加文字标签")
}

// 添加多个点的标注
const add21 = () => {
  const bounds = map.value.getBounds();
  const sw = bounds.getSouthWest();
  const ne = bounds.getNorthEast()
  const lngSpan = Math.abs(sw.lng - ne.lng)
  const latSpan = Math.abs(ne.lat - sw.lat)
  for (const i = 0; i < 25; i++) {
    const point = new T.LngLat(sw.lng + lngSpan * (Math.random() * 0.7), ne.lat - latSpan * (Math.random() * 0.7))
    const marker3 = new T.Marker(point)
    map.value.addOverLay(marker3)
  }
}

// 图片覆盖物类
function add22() {
  map.value.centerAndZoom(new T.LngLat(116.390750, 39.916980), 15)
  const bd = new T.LngLatBounds(
      new T.LngLat(116.385360, 39.911380),
      new T.LngLat(116.395940, 39.921400))
  const img = new T.ImageOverlay("https://tse3-mm.cn.bing.net/th/id/OIP-C.8smOs3ToMbkAJkS6_FMqlwHaEK?w=298&h=180&c=7&r=0&o=7&dpr=2&pid=1.7&rm=3", bd, {
    opacity: 1,
    alt: "故宫博物院"
  })
  map.value.addOverLay(img)
}

//  标注工具
function openMarkerTool() {
  if (!markerTool.value) return;
  closeRectangleTool();
  closeEditModes();
  markerTool.value.open();
  proxy.$modal.msgSuccess("已开启标注工具,点击地图即可添加标记")
}

function closeMarkerTool() {
  if (!markerTool.value) return;
  markerTool.value.close();
  proxy.$modal.msgSuccess("已关闭标注工具")
}

function editMarkers() {
  editingMode.value = !editingMode.value;
  if (editingMode.value) {
    closeRectangleTool();
    editingRectMode.value = false;
    markers.value.forEach(marker => {
      marker.enableDragging();
      marker.addEventListener("dragend", e => {
        const point = e.currentLnglat;
        proxy.$modal.msgSuccess(`标记已移动到：${point.lng.toFixed(6)}, ${point.lat.toFixed(6)}`)
      });
    });
    proxy.$modal.msgSuccess("标记编辑模式已启用，可以拖动标记");
  } else {
    closeEditModes();
  }
}

function removeMarker(marker) {
  map.value.removeOverLay(marker);
  markers.value = markers.value.filter(m => m !== marker);
}

function handleMarkerAdded(e) {
  const marker = e.marker;
  markers.value.push(marker);

  marker.addEventListener("dblclick", () => {
    removeMarker(marker);
    proxy.$modal.msgSuccess("标记已删除");
  });

  addMarkerContextMenu(marker);

  proxy.$modal.msgSuccess(`已添加标记 (${markers.value.length})`);
}

function addMarkerContextMenu(marker) {
  const T = window.T;
  const menu = new T.ContextMenu();

  menu.addItem({
    text: "查看坐标",
    callback: () => {
      const point = marker.getLngLat();
      proxy.$modal.alert(`经度: ${point.lng.toFixed(6)}<br>纬度: ${point.lat.toFixed(6)}`, "坐标信息", {
        dangerouslyUseHTMLString: true,
        confirmButtonText: "确定"
      });
    }
  });

  menu.addItem({
    text: "删除标记",
    callback: () => {
      removeMarker(marker);
      proxy.$modal.msgSuccess("标记已删除");
    }
  });

  marker.addEventListener("rightclick", e => {
    menu.open(e.lnglat);
  });
}

//  矩形工具

function openRectangleTool() {
  if (!rectTool.value) return;
  closeMarkerTool();
  closeEditModes();
  rectTool.value.open();
  proxy.$modal.msgSuccess("矩形工具已开启，在地图上拖动绘制矩形");
}

function closeRectangleTool() {
  if (!rectTool.value) return;
  rectTool.value.close();
  proxy.$modal.msg("矩形工具已关闭");
}

function editRectangles() {
  editingRectMode.value = !editingRectMode.value;

  if (editingRectMode.value) {
    closeMarkerTool();
    editingMode.value = false;

    rectangles.value.forEach(rect => {
      rect.enableEditing();
      rect.addEventListener("edit", e => {
        const newBounds = e.currentBounds;
        const area = calculateRectangleArea(newBounds);
        proxy.$modal.msgSuccess(`矩形已更新，新面积: ${area.toFixed(2)} 平方公里`);
      });
    });
    proxy.$modal.msgSuccess("矩形编辑模式已启用，可以拖动顶点调整矩形");
  } else {
    closeEditModes();
  }
}

function removeRectangle(rectangle) {
  map.value.removeOverLay(rectangle);
  rectangles.value = rectangles.value.filter(r => r !== rectangle);
}

function handleRectangleDrawn(e) {
  const rect = e.currentBounds;
  const rectangle = new T.Rectangle(rect, {
    color: "#409EFF",
    weight: 2,
    opacity: 0.8,
    fillColor: "#409EFF",
    fillOpacity: 0.2
  });

  map.value.addOverLay(rectangle);
  rectangles.value.push(rectangle);

  rectangle.addEventListener("click", () => {
    const area = calculateRectangleArea(rect);
    proxy.$modal.msg(`矩形面积: ${area.toFixed(2)} 平方公里`);
  });

  setTimeout(() => {
    addRectangleContextMenu(rectangle, rect);
  }, 100);

  proxy.$modal.msgSuccess(`已添加矩形 (${rectangles.value.length})`);
}

function addRectangleContextMenu(rectangle, bounds) {
  if (!map.value || !document.getElementById("mapContainer")) {
    console.error("地图容器未找到");
    return;
  }

  const menu = new T.ContextMenu({
    map: map.value,
    width: 150
  });

  menu.addItem({
    text: "查看坐标范围",
    callback: () => {
      const sw = bounds.getSouthWest();
      const ne = bounds.getNorthEast();
      proxy.$modal.alert(`
            <p><b>西南角坐标:</b></p>
            <p>经度: ${sw.lng.toFixed(6)}</p>
            <p>纬度: ${sw.lat.toFixed(6)}</p>
            <p><b>东北角坐标:</b></p>
            <p>经度: ${ne.lng.toFixed(6)}</p>
            <p>纬度: ${ne.lat.toFixed(6)}</p>
          `, "矩形坐标范围", {
        dangerouslyUseHTMLString: true,
        confirmButtonText: "确定"
      });
    }
  });

  menu.addItem({
    text: "删除矩形",
    callback: () => {
      removeRectangle(rectangle);
      proxy.$modal.msgSuccess("矩形已删除");
    }
  });
  rectangle.addEventListener("rightclick", e => {
    if (e.lnglat) {
      menu.open(e.lnglat);
    }
  });
}

function calculateRectangleArea(bounds) {
  console.debug("开始计算矩形面积，传参bounds：{}", bounds)
  const sw = bounds.kq;
  const ne = bounds.Lq;
  console.debug("矩形左下角为：{}，右上角为：{}", sw, ne)

  const lngDiff = Math.abs(ne.lng - sw.lng);
  const latDiff = Math.abs(ne.lat - sw.lat);

  const midLat = (sw.lat + ne.lat) / 2;
  const latKm = latDiff * 111;
  const lngKm = lngDiff * 111 * Math.cos((midLat * Math.PI) / 180);

  return latKm * lngKm;
}

//  测距/测面工具

function openLineTool() {
  lineTool.value.open();
  proxy.$modal.msgSuccess("已开启测距")
}

function closeLineTool() {
  lineTool.value.close();
  proxy.$modal.msgSuccess("已关闭测距")
}

function openPolygonTool() {
  polygonTool.value.open();
  proxy.$modal.msgSuccess("已开启测面")
}

function closePolygonTool() {
  polygonTool.value.close();
  proxy.$modal.msgSuccess("已关闭测面")
}

//  画圆工具

function openCircleTool() {
  circleTool.value.open();
  proxy.$modal.msgSuccess("已开启画圆")
}

function closeCircleTool() {
  circleTool.value.close();
  proxy.$modal.msgSuccess("已关闭画圆")
}

function onDrawCircle(center, radius) {
  document.getElementById('info').value = "圆的半径是 " + parseInt(radius) + " 米";
}

//  地图组件

function startPicker() {
  if (coordinatePicker.value) {
    coordinatePicker.value.removeEvent();
  }
  const T = window.T;
  coordinatePicker.value = new T.CoordinatePickup(map.value, {
    callback: (lnglat) => {
      handlePickedCoordinate(lnglat);
    }
  });
  coordinatePicker.value.addEvent();
  proxy.$modal.msgSuccess("坐标拾取器已启用，请在地图上点击获取坐标");
}

function handlePickedCoordinate(lnglat) {
  position.value = lnglat.lng.toFixed(6) + "," + lnglat.lat.toFixed(6);
}

function addLineGrid() {
  if (glayer.value) {
    glayer.value = null;
  }
  glayer.value = new T.GridlineLayer({});
  map.value.addLayer(glayer.value)
  proxy.$modal.msgSuccess("格网图层添加成功")
}

function removeLineGrid() {
  if (!glayer.value) {
    proxy.$modal.msgWarning("暂无格网，无需移除")
    return;
  }
  map.value.removeLayer(glayer.value)
  proxy.$modal.msgSuccess("格网图层已移除")
}

function addPaintBrush() {
  handler.value.open();
  proxy.$modal.msgSuccess("画笔工具已开启")
  console.debug("Handler对象：", handler.value);
  console.debug("addEventlistener类型：", typeof handler.value.addEventListener);
}

function closePaintBrush() {
  handler.value.removeEventListener && handler.value.removeEventListener("draw");
  handler.value.close();
  proxy.$modal.msgSuccess("画笔工具已关闭")
}

function clearDraw() {
  map.value.clearOverLays();
  proxy.$modal.msgSuccess("清除绘制成功")
}

//  通用方法

function closeEditModes() {
  editingMode.value = false;
  editingRectMode.value = false;
  markers.value.forEach(marker => {
    marker.disableDragging();
  })
  rectangles.value.forEach(rectangles => {
    rectangles.disableEditing();
  })
}

function clearAllMarkers() {
  proxy.$modal.confirm("确定要清楚所有标记吗？").then(() => {
    markers.value.forEach(marker => {
      map.value.removeOverLay(marker);
    });
    markers.value = [];
    closeEditModes();
    proxy.$modal.msgSuccess("已清除所有标记")
  }).catch(() => {
  })
}

function clearAllRectangles() {
  proxy.$modal.confirm("确定要清除所有矩形吗？").then(() => {
    rectangles.value.forEach(rect => {
      map.value.removeOverLay(rect);
    });
    rectangles.value = [];
    closeEditModes();
    proxy.$modal.msgSuccess("已清除所有矩形")
  }).catch(() => {
  })
}

// 定位
const localCity = ref(null)
const  localH5 = ref(null)
const geoLocalCity = () => {
  proxy.$modal.msgWarning("天地图 JS API 4.0的ip定位需要HTTPS,开发环境中的小问题待解决")
  return;
  localCity.value = new T.LocalCity();
  localCity.value.location(function (e) {
    var marker = new T.Marker(e.lnglat);
    map.value.addOverLay(marker);
    proxy.$modal.closeLoading();
  })
}
function geoLocation() {
  proxy.$modal.loading("正在获取定位坐标...")
  localH5.value = new T.Geolocation();
  var fn = function (e) {
    map.value.centerAndZoom(e.lnglat,18)
    proxy.$modal.closeLoading();
    proxy.$modal.msgSuccess(`获取定位坐标成功，坐标为： ${e.lnglat.lng},${e.lnglat.lat}`);
    var marker = new T.Marker(e.lnglat);
    map.value.addOverLay(marker);
  }
  localH5.value.getCurrentPosition(fn)
}
//  生命周期
onMounted(() => {
  setTimeout(() => {
    initMap()
  }, 1000)
})

onUnmounted(() => {
  if (map.value) {
    map.value.destroy();
  }
})
</script>

<style scoped lang="scss">

</style>