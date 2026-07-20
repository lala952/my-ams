<template>
  <div class="main">
    <!-- 单个模式：显示表单 -->
    <el-form
        v-if="!isBatchMode"
        ref="formRef"
        :model="form"
        :rules="rules"
        :inline="true"
        :disabled="props.isView"
    >
      <el-form-item label="存放位置" prop="location" style="width: 42%">
        <el-input
            v-model="form.location"
            clearable
            placeholder="请输入存放位置"
            style="width: 100%"
            show-word-limit
            maxlength="200"
        />
      </el-form-item>
      <el-form-item label="经度" prop="longitude">
        <el-input
            v-model="form.longitude"
            clearable
            placeholder="请输入经度或地图选点"
            @input="onLngLatInput"

        />
      </el-form-item>
      <el-form-item label="纬度" prop="latitude">
        <el-input
            v-model="form.latitude"
            clearable
            placeholder="请输入纬度或地图选点"
            @input="onLngLatInput"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">确定</el-button>
        <el-button @click="resetQuery">清空</el-button>
      </el-form-item>
    </el-form>
    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            @click="geoLocation"
        >
          定位到当前位置
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            @click="handleChoseLnglat"
            v-hasPermi="['asset:assets:edit']"
            v-show="!props.isView"
        >选择坐标
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            @click="handleGeoCode"
            v-show="!props.isView"
        >选择坐标并填充
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            @click="handleCurrentMarkerEnableDragging"
            v-show="!props.isView"
        >点拖拽
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            @click="handleCurrentMarkerDisableDragging"
            v-show="!props.isView"
        >点不可拖拽
        </el-button>
      </el-col>
    </el-row>
    <!-- 地图容器 -->
    <div
        id="mapContainer"
        style="width: 100%; height: 500px;"
    />
  </div>
</template>

<script setup lang="js">
import {
  getCurrentInstance,
  onMounted,
  onUnmounted,
  reactive,
  ref,
  watch,
  computed,
} from "vue";

/** 获取当前组件实例 */
const {proxy} = getCurrentInstance();

/** 定义父传子数据 */
const modelValue = defineModel({
  type: [Object, Array],
  default: () => ({}),
});
const props = defineProps({
  // 是否只读，真假值用来预防点击标注
  isView: {
    type: Boolean,
    default: true,
  },
});
/** 定义子传父数据 */
const emit = defineEmits(["submit"]);

/** 地图相关 */
const map = ref(null);
const mapCenter = ref(new T.LngLat(116.39125, 39.90812))
const zoom = ref(15)
const lngLatTimer = ref(null);
const isInternalUpdate = ref(false);
const currentMarker = ref(null);
const markers = ref([]);

/** 表单数据 */
const form = reactive({
  location: null,
  longitude: null,
  latitude: null,
});

/** 表当校验规则 */
const rules = ref({
  location: [
    {
      type: "string",
      required: true,
      message: "请输入存放位置",
      trigger: "blur",
    },
    {
      validator: (rule, value, callback) => {
        if (value.length > 200) {
          callback(new Error("存放位置不能超过200个字符"));
        } else {
          callback();
        }
      }
    }
  ],
  longitude: [
    {
      required: true,
      message: "请选择资产存放经度",
      trigger: "blur",
    },
    {
      validator: (rule, value, callback) => {
        if (typeof value !== 'number') {
          callback(new Error("请输入正确的经度"));
        } else {
          callback();
        }
      }
    }
  ],
  latitude: [
    {
      required: true,
      message: "请选择资产存放纬度",
      trigger: "blur",
    },
    {
      validator: (rule, value, callback) => {
        if (typeof value !== 'number') {
          callback(new Error("请输入正确的纬度"));
        } else {
          callback();
        }
      }
    }
  ]
})

/** 判断是否批量模式（计算属性会动态更新） */
const isBatchMode = computed(() => Array.isArray(modelValue.value));

/** 监听 props 变化 将modelValue作为监听对象，支持单个和批量两种模式 */
watch(
    () => modelValue.value,
    (val) => {
      // 1.防抖，避免表单数据变化时，地图数据未及时更新
      if (isInternalUpdate.value) return;

      // 2.如果是单个模式，且监听到modelValue有变化时，赋值给表单form对象
      if (!isBatchMode.value && val) {
        clearAllMarkers()
        form.location = val.location || null;
        form.longitude = val.longitude || null;
        form.latitude = val.latitude || null;
        // 2.1 如果存在经纬度，则添加标注
        if (map.value && form.longitude && form.latitude) {
          const lng = parseFloat(form.longitude);
          const lat = parseFloat(form.latitude);
          if (!isNaN(lng) && !isNaN(lat)) {
            const lnglat = new T.LngLat(lng, lat);
            map.value.centerAndZoom(lnglat, 15);
            addMarker(lnglat, form.location);
          }
        }
      } else if (isBatchMode.value && val && map.value) { // 如果是批量模式，则批量添加标注
        clearAllMarkers();
        val.forEach((asset) => {
          if (asset.longitude && asset.latitude) {
            const lng = parseFloat(asset.longitude);
            const lat = parseFloat(asset.latitude);
            if (!isNaN(lng) && !isNaN(lat)) {
              addMarkers(new T.LngLat(lng, lat), asset);
            }
          }
        });
        calculateAndSetMapCenter();
      }
    },
    {immediate: true, deep: true},
);

// 监听本地表单变化，同步给父组件（仅单个模式）
watch(
    form,
    (val) => {
      if (!isInternalUpdate.value && !isBatchMode.value) {
        isInternalUpdate.value = true;
        modelValue.value = {...val};
        setTimeout(() => {
          isInternalUpdate.value = false;
        }, 50);
      }
    },
    {deep: true},
);

// 输入经纬度时，同步给地图，防抖处理
function onLngLatInput() {
  if (lngLatTimer.value) clearTimeout(lngLatTimer.value);
  lngLatTimer.value = setTimeout(() => {
    if (!map.value) return;
    const lng = parseFloat(form.longitude);
    const lat = parseFloat(form.latitude);
    if (!isNaN(lng) && !isNaN(lat)) {
      const lnglat = new T.LngLat(lng, lat);
      map.value.centerAndZoom(lnglat, 15);
      addMarker(lnglat);
    }
  }, 500);
}

function submit() {
  proxy.$refs["formRef"].validate((valid) => {
    if (valid) {
      emit("submit", {...form});
    }
  });
}

function resetQuery() {
  isInternalUpdate.value = true;
  form.location = null;
  form.longitude = null;
  form.latitude = null;
  isInternalUpdate.value = false;
  if (map.value) {
    map.value.clearOverLays();
    currentMarker.value = null;
    markers.value = [];
  }
}


/** 地图初始化 */
function initMap() {
  const T = window.T;
  if (!T) {
    console.error('天地图 API 未加载');
    return;
  }

  map.value = new T.Map('mapContainer');

  setCenterAndZoom() // 设置地图中心点和缩放级别
  addVectorLayer() // 添加矢量地图层和文字标注层
  addAnnotionLayer()
  addZoomControl() // 添加控件（缩放控件和比例尺控件）
  addScaleControl() // 如果传递过来的是一个对象，这展示单个标注即可
  addMarkerByModelValue() // 如果传递过来是一个对象，这展示单个标注即可
  addMarkersByModelValue() // 如果传过来的是数组，这通过foreach进行遍历，添加多个标记
}

/** 设置地图中心点和缩放级别 */
function setCenterAndZoom() {
  // 1. 传递的是对象
  if (!isBatchMode.value && form.longitude && form.latitude) {
    const lng = parseFloat(form.longitude);
    const lat = parseFloat(form.latitude);
    if (!isNaN(lng) && !isNaN(lat)) {
      mapCenter.value = new T.LngLat(lng, lat);
      console.debug("情况一：传递的modelValue是对象", modelValue.value, mapCenter.value)
    }
  }
  // 2.传递的是数组
  if (isBatchMode.value && modelValue.value.length > 0) {
    const first = modelValue.value.find((a) => a.longitude && a.latitude);
    if (first) {
      const lng = parseFloat(first.longitude);
      const lat = parseFloat(first.latitude);
      if (!isNaN(lng) && !isNaN(lat)) {
        mapCenter.value = new T.LngLat(lng, lat);
        console.debug("情况二：传递的modelValue是数组", modelValue.value, mapCenter.value)

      }
    }
  }

  map.value.centerAndZoom(mapCenter.value, zoom.value);
  console.debug("最终设置的中心点和缩放级别分别为：", map.value.getCenter(), map.value.getZoom())
}

/** 添加矢量地图层 */
function addVectorLayer() {
  // 矢量地图
  const vectorLayer = new T.TileLayer(
      "http://t0.tianditu.gov.cn/vec_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=66a6de480dbec1a0b2393cd1f643096d",
      {
        minZoom: 1,
        maxZoom: 18,
      },
  );
  map.value.addLayer(vectorLayer);
}

/** 添加文字标注层 */
function addAnnotionLayer() {
  // 文字标注层
  const annotionLayer = new T.TileLayer(
      "http://t0.tianditu.gov.cn/cva_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cva&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=66a6de480dbec1a0b2393cd1f643096d",
      {
        minZoom: 1,
        maxZoom: 18,
      },
  );
  map.value.addLayer(annotionLayer);
}

/** 添加缩放控件 */
function addZoomControl() {
  const zoomControl = new T.Control.Zoom();
  zoomControl.setPosition(T_ANCHOR_BOTTOM_RIGHT);
  map.value.addControl(zoomControl);
}

/** 添加比例尺控件 */
function addScaleControl() {
  const scaleControl = new T.Control.Scale();
  scaleControl.setPosition(T_ANCHOR_BOTTOM_LEFT);
  map.value.addControl(scaleControl);
}

/** 添加圆形 */
function addCircle(lnglat) {
  var circle = new T.Circle(lnglat, 10, {
    color: "#3778c8",
    weight: 1.5,
    opacity: 0.5,
    fillColor: "#fff",
    fillOpacity: 0.5,
    lineStyle: "solid",
  })
  map.value.addOverLay(circle);
}

/** 浏览器定位 */
function geoLocation() {
  proxy.$modal.loading("正在定位，请稍等...");
  const geolocation = new T.Geolocation();
  var fn = function (e) {
    // 如果是是编辑状态，则清除所有覆盖物，定位到当前位置
    if (props.isView === false) {
      map.value.clearOverLays();
    }
    console.debug(e)
    console.debug(typeof e)
    map.value.centerAndZoom(e.lnglat, 18)
    proxy.$modal.closeLoading();
    proxy.$modal.msgSuccess(`定位成功，当前坐标为: ${e.lnglat.lng},${e.lnglat.lat}`);

    var marker = new T.Marker(e.lnglat);
    map.value.addOverLay(marker);

    const html = `<p><b>当前位置坐标：</b>${e.lnglat.lng},${e.lnglat.lat}</p>`;
    const infoWin = new T.InfoWindow(html);

    marker.openInfoWindow(infoWin);

    setTimeout(() => {
      marker.closeInfoWindow()
    }, 10000)

    marker.addEventListener("click", () => {
      marker.openInfoWindow(infoWin);
    });

    form.longitude = e.lnglat.lng;
    form.latitude = e.lnglat.lat;
  };
  geolocation.getCurrentPosition(fn);
}

/** 开启标注选择地点经纬度 */
function handleChoseLnglat() {
  if (isBatchMode.value || props.isView) {
    proxy.$modal.msgWarning("当前模式只读，无法进行经纬度位置选择")
    return;
  }
  proxy.$modal.msgSuccess("请点击地图选择经纬度")
  map.value.clearOverLays();
  map.value.addEventListener("click", (e) => {
    isInternalUpdate.value = true;
    form.longitude = e.lnglat.getLng();
    form.latitude = e.lnglat.getLat();
    isInternalUpdate.value = false;
    addMarker(e.lnglat);
  });
}

/** 根据传过来的数据添加标注 */
function addMarkerByModelValue() {
  if (!isBatchMode.value && form.longitude && form.latitude) {
    const lng = parseFloat(form.longitude)
    const lat = parseFloat(form.latitude)
    if (!isNaN(lng) && !isNaN(lat)) {
      addMarker(new T.LngLat(lng, lat), form.location);
    }
  }
}

/** 根据坐标点进行范地址解析 填充到 form.locatoin */
function handleGeoCode() {
  const geocode = new T.Geocoder();
  map.value.addEventListener("click", (e) => {
    clearAllMarkers()
    geocode.getLocation(e.lnglat, searchResult)
    form.longitude = e.lnglat.lng
    form.latitude = e.lnglat.lat
    currentMarker.value = new T.Marker(new T.LngLat(e.lnglat.lng, e.lnglat.lat))
    map.value.addOverLay(currentMarker.value)
  })
}

function searchResult(result) {
  if (result.getStatus() === 0) {
    form.location = result.getAddress();
    console.debug("***经纬度反编码成功，result如下")
    console.debug(result)
    console.debug("result响应状态：" + result.getStatus())
    console.debug("result响应信息：" + result.getMsg)
  } else {
    proxy.$modal.msgError("地址解析失败,请联系系统管理员")
    console.debug("地址解析失败" + "服务器返回状态" + result.getStatus())
    console.debug("地址解析失败" + "服务器返回响应信息" + result.getMsg())
  }
}

/** 处理当前标记点可拖拽与否 */
function handleCurrentMarkerEnableDragging() {
  if (!currentMarker.value) {
    proxy.$modal.msgWarning("无法启用点拖拽,当前地图中并无标记")
  }
  currentMarker.value.enableDragging()
  proxy.$modal.msgSuccess("已启用点拖拽，当前标记点已可拖拽")
}

watch(() => currentMarker.value?.getLngLat(),
    (lnglat) => {
      if (!lnglat) return;
      console.debug("当前标记点经纬度已更新");
      console.debug(currentMarker.value.getLngLat())
      form.longitude = lnglat.lng;
      form.latitude = lnglat.lat;
      const geocode = new T.Geocoder();
      geocode.getLocation(lnglat, searchResult)
    })

function handleCurrentMarkerDisableDragging() {
  if (!currentMarker.value) {
    proxy.$modal.msgWarning("无需禁用点拖拽,当前地图中并无标记")
  }
  currentMarker.value.disableDragging()
  proxy.$modal.msgSuccess("已成功禁用标记")
}

/** 根据传过来的数据批量添加标注 */
function addMarkersByModelValue() {
  if (isBatchMode.value) {
    modelValue.value.forEach((asset) => {
      if (asset.longitude && asset.latitude) {
        const lng = parseFloat(asset.longitude);
        const lat = parseFloat(asset.latitude);
        if (!isNaN(lng) && !isNaN(lat)) {
          addMarkers(new T.LngLat(lng, lat), asset);
        }
      }
    });
    calculateAndSetMapCenter();
  }
}

/** 添加单个标记 */
function addMarker(lnglat, name) {
  if (!map.value) return;
  if (currentMarker.value) {
    map.value.removeOverLay(currentMarker.value);
  }
  currentMarker.value = new T.Marker(lnglat);
  map.value.addOverLay(currentMarker.value);
  if (name) {
    const html = `<p><b>存放位置：</b>${name}</p>`;
    const infoWin = new T.InfoWindow(html);
    currentMarker.value.addEventListener("click", () => {
      currentMarker.value.openInfoWindow(infoWin);
    });
  }
}

/** 批量添加标记 */
function addMarkers(lnglat, asset) {
  if (!map.value) return;
  const marker = new T.Marker(lnglat);
  map.value.addOverLay(marker);

  const html = `
    <div>
      <h4 style="margin:3px;">资产名称：${asset.assetName}</h4>
      <p style="margin:2px;">资产编码：<span  class="link-type">${asset.assetCode}</span></p>
      <p style="margin:2px;">存放位置：${asset.location || "-"}</p>
    </div>
  `;
  const infoWin = new T.InfoWindow(html);
  marker.addEventListener("click", () => {
    markers.value.forEach((m) => m.closeInfoWindow());
    marker.openInfoWindow(infoWin);
  });

  markers.value.push(marker);
}

/** 清除所有标记 */
function clearAllMarkers() {
  if (!map.value) return;
  markers.value.forEach((m) => map.value.removeOverLay(m));
  markers.value = [];
  if (currentMarker.value) {
    map.value.removeOverLay(currentMarker.value);
    currentMarker.value = null;
  }
}

/** 计算并设置中心点 */
function calculateAndSetMapCenter() {
  // 1. 校验
  if (!map.value || markers.value.length === 0) return;
  // 2.通过数组的map方法，将所有点的经纬度取出来
  const lnglats = markers.value.map((m) => m.getLngLat());
  // 3.如果只有一个点，则直接定位到这个点
  if (lnglats.length === 1) {
    map.value.centerAndZoom(lnglats[0], 15);
  } else { // 如果是多个点，则计算所有点的中心
    let sumLng = 0, sumLat = 0;
    lnglats.forEach((ll) => {
      sumLng += ll.getLng();
      sumLat += ll.getLat();
    });
    const center = new T.LngLat(
        sumLng / lnglats.length,
        sumLat / lnglats.length,
    );
    map.value.centerAndZoom(center, 13);
  }
}

/** DOM 挂载后进行初始化 */
onMounted(() => {
  setTimeout(() => {
    initMap();
  }, 1000);
});

/** 组件销毁时进行销毁 */
onUnmounted(() => {
  if (lngLatTimer.value) clearTimeout(lngLatTimer.value);
  if (map.value) {
    map.value.destroy();
  }
});
</script>

<style scoped lang="scss"></style>
