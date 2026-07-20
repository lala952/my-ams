<template>
  <div id="mapContainer" style="width:100%;height:90%"/>
  <div class="mt20" style="display: flex;justify-content: flex-start">
    <el-button link text type="primary" @click="calDistance()">当前位置与资产的距离</el-button>
  </div>
</template>

<script setup>
import {getCurrentInstance, onBeforeUnmount, onMounted, ref, watch} from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'

const {proxy} = getCurrentInstance();
const props = defineProps({
  assets: {type: [Object, Array], default: null},
  longitude: {type: Number, default: null},
  latitude: {type: Number, default: null}
})

const emit = defineEmits(['update:longitude', 'update:latitude'])

// let map = null
const map = ref(null)
let pickerMarker = null
let assetMarkers = []
let AMapInstance = null


function clearAllAssetMarkers() {
  assetMarkers.forEach(m => map.value.remove(m))
  assetMarkers = []
}

function showAssetsOnMap(data) {
  if (!map.value || !AMapInstance || !data) return

  const list = Array.isArray(data) ? data : [data]
  const validList = list.filter(item => item?.longitude && item?.latitude)
  if (validList.length === 0) return

  clearAllAssetMarkers()

  validList.forEach(item => {
    const marker = new AMapInstance.Marker({
      position: [item.longitude, item.latitude],
      icon: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png',
      anchor: 'bottom-center',
      offset: [0, -10]
    })
    const infoWindow = new AMapInstance.InfoWindow({
      content: `
        <div style="padding:8px 12px;font-size:13px;line-height:1.8;min-width:180px">
          <div><strong>资产编码:</strong> ${item.assetCode || '-'}</div>
          <div><strong>资产名称:</strong> ${item.assetName || '-'}</div>
          <div><strong>经纬度:</strong> [${item.longitude}, ${item.latitude}]</div>
        </div>
      `,
      offset: [0, -30]
    })
    marker.on('click', () => infoWindow.open(map.value, marker.getPosition()))
    map.value.add(marker)
    assetMarkers.push(marker)
    setTimeout(() => {
      infoWindow.close(map.value)
      proxy.$modal.msg(`资产信息窗口已自动关闭，您可点击标记重新打开`)
    },3000)
  })

  if (validList.length === 1) {
    const item = validList[0]
    map.value.setZoomAndCenter(16, [item.longitude, item.latitude])
    const firstMarker = assetMarkers[0]
    firstMarker.emit('click')
  } else {
    map.value.setFitView(null, false, [80, 80, 80, 80])
  }
}

watch(() => props.assets, (val) => {
  showAssetsOnMap(val)
}, {deep: true})

const initMap = () => {
  AMapLoader.load({
    key: '518e0f027c8f3814e9f84505b879ed5b',
    version: '2.0',
    plugins: ['AMap.ToolBar', 'AMap.Scale', 'AMap.Geolocation']
  }).then((AMap) => {
    AMapInstance = AMap
    map.value = new AMap.Map('mapContainer', {
      zoom: 12,
      center: [104.148223, 30.672392]
    })
    map.value.addControl(new AMap.ToolBar({
      offset: [10, 100],        // 偏移量，相对于地图左上角或右下角，单位：像素
      position: 'RB',          // 控件停靠位置，可选值：'LT'左上, 'RT'右上, 'LB'左下, 'RB'右下
      ruler: true,             // 是否显示标尺
      noIpLocate: false,       // 定位失败时是否显示IP定位按钮，默认true
      locate: true,            // 是否显示定位按钮，默认true
      liteStyle: false         // 是否精简模式，默认false
    }))
    map.value.addControl(new AMap.Scale())

    addGeolocationControl()

    map.value.on('click', (e) => {
      const lng = e.lnglat.getLng()
      const lat = e.lnglat.getLat()
      if (pickerMarker) map.value.remove(pickerMarker)
      pickerMarker = new AMap.Marker({
        position: [lng, lat],
        icon: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_bs.png',
        anchor: 'bottom-center',
        offset: [0, -10]
      })
      map.value.add(pickerMarker)
      emit('update:longitude', lng)
      emit('update:latitude', lat)
    })

    if (props.longitude && props.latitude) {
      map.value.setZoomAndCenter(16, [props.longitude, props.latitude])
      pickerMarker = new AMap.Marker({
        position: [props.longitude, props.latitude],
        icon: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_bs.png',
        anchor: 'bottom-center',
        offset: [0, -10]
      })
      map.value.add(pickerMarker)
    }

    if (props.assets) {
      showAssetsOnMap(props.assets)
    }
  }).catch(e => console.error('Map loading failed:', e))
}

// 2.6 浏览器定位
const geolocation = ref(null);

function addGeolocationControl() {
  // 添加定位控件
  geolocation.value = new AMap.Geolocation({
    enableHighAccuracy: true,
    timeout: 10000,
    offset: [10, 10],
    postion: 'RB'
  })
  map.value.addControl(geolocation.value)
}

function handleGeolocation() {
  geolocation.value.getCurrentPosition((status, result) => {
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
    // proxy.$modal.msgError(`定位失败，请检查是否开启定位权限`)
  }
}

// 我想计算两点之间的距离 todo
const calDistance = () => {
  handleGeolocation();

}
onMounted(() => initMap())
onBeforeUnmount(() => {
  if (map.value) map.value.destroy()
})
</script>