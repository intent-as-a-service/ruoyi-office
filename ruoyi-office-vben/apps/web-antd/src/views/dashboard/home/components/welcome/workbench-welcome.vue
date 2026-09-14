<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';

import { useUserStore } from '@vben/stores';

// @ts-ignore - solarlunar 没有类型定义
import { solar2lunar } from 'solarlunar';

interface Props {
  greeting?: string; // 自定义提示语
  showWeather?: boolean; // 是否显示天气
  weatherApiKey?: string; // 高德地图天气API Key
  defaultCity?: string; // 默认城市名称（如：北京、上海）
}

const props = withDefaults(defineProps<Props>(), {
  greeting: '欢迎回来，开始您的工作吧！',
  showWeather: true,
  weatherApiKey: 'b7e576ee7562587ff8acdcea709e41b6', // 高德地图API Key，需要申请：https://console.amap.com/
  defaultCity: '北京', // 默认城市
});

const userStore = useUserStore();

// 天气信息
const weather = ref<{
  city: string;
  humidity?: string;
  icon: string;
  iconCode?: string; // 天气图标代码
  temp: string;
  text: string;
  windDir?: string;
} | null>(null);

const weatherLoading = ref(false);

// 天气类型（用于背景装饰）
const weatherType = computed(() => {
  if (!weather.value?.iconCode) return 'clear';

  const code = weather.value.iconCode;
  // 高德地图天气代码映射
  // 晴
  if (code === '01') return 'clear';
  // 多云/阴
  if (['02', '03', '04'].includes(code)) return 'cloudy';
  // 雨天
  if (
    [
      '05',
      '06',
      '07',
      '08',
      '09',
      '10',
      '11',
      '12',
      '13',
      '14',
      '15',
      '16',
      '17',
      '18',
      '19',
      '20',
      '21',
      '22',
      '23',
      '24',
      '25',
    ].includes(code)
  )
    return 'rainy';
  // 雪天
  if (
    [
      '26',
      '27',
      '28',
      '29',
      '30',
      '31',
      '32',
      '33',
      '34',
      '35',
      '36',
      '37',
      '38',
    ].includes(code)
  )
    return 'snowy';
  // 雾霾/沙尘
  if (
    [
      '53',
      '54',
      '55',
      '56',
      '57',
      '58',
      '59',
      '60',
      '61',
      '62',
      '63',
      '64',
      '65',
      '66',
      '67',
      '68',
      '69',
      '70',
      '71',
      '72',
      '73',
      '74',
      '75',
      '76',
      '77',
      '78',
      '79',
      '80',
      '81',
      '82',
      '83',
      '84',
      '85',
      '86',
      '87',
      '88',
      '89',
      '90',
      '91',
      '92',
      '93',
      '94',
      '95',
      '96',
      '97',
      '98',
      '99',
    ].includes(code)
  )
    return 'foggy';

  return 'clear';
});

// 获取当前时间段的问候语
const timeGreeting = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '凌晨好';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 18) return '下午好';
  if (hour < 22) return '晚上好';
  return '夜深了';
});

// 获取当前日期
const currentDate = computed(() => {
  const now = new Date();
  const weekDays = ['日', '一', '二', '三', '四', '五', '六'];
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const weekDay = weekDays[now.getDay()];
  return `${year}-${month}-${day} 星期${weekDay}`;
});

// 获取农历日期（使用 solarlunar 库）
const lunarDate = computed(() => {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1;
  const day = now.getDate();

  try {
    const lunar = solar2lunar(year, month, day);
    if (lunar && lunar.lYear) {
      // 格式化农历日期：天干地支年 + 生肖 + 月份 + 日期
      // solarlunar 返回的属性名是小写的：animal, monthCn, dayCn
      // monthCn 已经包含了"月"字，如"冬月"、"正月"等
      const yearText = `${lunar.gzYear}${lunar.animal}年`;
      const monthText = lunar.isLeap ? `闰${lunar.monthCn}` : lunar.monthCn;
      const dayText = lunar.dayCn;

      return `${yearText} ${monthText}${dayText}`;
    }
  } catch (error) {
    console.error('农历转换失败:', error);
  }

  return '农历';
});

// 获取天气图标（高德地图天气代码）
function getWeatherIcon(iconCode: string) {
  // 高德地图天气代码映射到 iconify
  // 参考：https://lbs.amap.com/api/webservice/guide/api/weatherinfo
  const iconMap: Record<string, string> = {
    '01': 'wi:day-sunny', // 晴
    '02': 'wi:day-cloudy', // 少云
    '03': 'wi:cloudy', // 晴间多云
    '04': 'wi:cloudy', // 多云
    '05': 'wi:cloudy', // 阴
    '06': 'wi:showers', // 有风
    '07': 'wi:cloudy', // 阴
    '08': 'wi:showers', // 阵雨
    '09': 'wi:rain', // 小到中雨
    '10': 'wi:rain', // 中雨
    '11': 'wi:rain-wind', // 中到大雨
    '12': 'wi:rain-wind', // 大到暴雨
    '13': 'wi:storm-showers', // 暴雨到大暴雨
    '14': 'wi:hurricane', // 大暴雨到特大暴雨
    '15': 'wi:rain', // 雨
    '16': 'wi:rain-mix', // 雨夹雪
    '17': 'wi:rain-mix', // 雨雪天气
    '18': 'wi:sprinkle', // 小雨
    '19': 'wi:rain', // 中雨
    '20': 'wi:rain-wind', // 大雨
    '21': 'wi:storm-showers', // 暴雨
    '22': 'wi:hurricane', // 大暴雨
    '23': 'wi:hurricane', // 特大暴雨
    '24': 'wi:thunderstorm', // 雷阵雨
    '25': 'wi:thunderstorm', // 强雷阵雨
    '26': 'wi:snow', // 小雪
    '27': 'wi:snow', // 中雪
    '28': 'wi:snow-wind', // 大雪
    '29': 'wi:snow-wind', // 暴雪
    '30': 'wi:snow', // 雪
    '31': 'wi:sleet', // 雨夹雪
    '32': 'wi:rain-mix', // 雨雪天气
    '33': 'wi:snow', // 阵雪
    '53': 'wi:fog', // 雾
    '54': 'wi:smog', // 霾
    '55': 'wi:smog', // 中度霾
    '56': 'wi:smog', // 重度霾
    '57': 'wi:smog', // 严重霾
    '58': 'wi:fog', // 大雾
    '59': 'wi:fog', // 浓雾
    '60': 'wi:fog', // 强浓雾
    '99': 'wi:na', // 未知
  };

  return iconMap[iconCode] || 'wi:day-sunny';
}

// 通过IP获取城市名称（使用高德地图IP定位）
async function getCityByIP(): Promise<null | string> {
  if (!props.weatherApiKey) {
    return null;
  }

  try {
    // 高德地图IP定位API
    const response = await fetch(
      `https://restapi.amap.com/v3/ip?key=${props.weatherApiKey}`,
    );
    const data = await response.json();

    if (data.status === '1' && data.city) {
      // 去除"市"后缀
      return data.city.replace(/市$/, '');
    }

    console.warn('高德地图IP定位失败:', data);
  } catch (error) {
    console.error('高德地图IP定位异常:', error);
  }

  return null;
}

// 获取天气信息（使用高德地图天气API）
async function fetchWeather() {
  if (!props.showWeather) {
    return;
  }

  weatherLoading.value = true;

  try {
    // 如果未配置API Key，使用默认数据
    if (!props.weatherApiKey) {
      weather.value = {
        city: props.defaultCity,
        temp: '22',
        text: '晴',
        icon: 'wi:day-sunny',
        iconCode: '01',
        humidity: '65%',
        windDir: '东南风',
      };
      weatherLoading.value = false;
      return;
    }

    // 步骤1: 通过IP获取城市名称（如果获取失败，使用默认城市）
    let cityName = await getCityByIP();
    if (!cityName) {
      cityName = props.defaultCity;
      console.warn('无法通过IP获取城市，使用默认城市:', cityName);
    }

    // 步骤2: 使用高德地图天气API获取实时天气
    // 高德地图天气API：https://restapi.amap.com/v3/weather/weatherInfo
    const weatherResponse = await fetch(
      `https://restapi.amap.com/v3/weather/weatherInfo?key=${props.weatherApiKey}&city=${encodeURIComponent(cityName)}&extensions=base`,
    );
    const weatherData = await weatherResponse.json();

    if (
      weatherData.status === '1' &&
      weatherData.lives &&
      weatherData.lives.length > 0
    ) {
      const live = weatherData.lives[0];
      weather.value = {
        city: live.city || cityName,
        temp: live.temperature,
        text: live.weather,
        icon: getWeatherIcon(live.weathercode),
        iconCode: live.weathercode,
        humidity: `${live.humidity}%`,
        windDir: `${live.winddirection}风 ${live.windpower}级`,
      };
    } else {
      throw new Error(
        `获取天气数据失败: ${weatherData.info || weatherData.status}`,
      );
    }
  } catch (error) {
    console.error('获取天气信息失败:', error);
    // 失败时使用默认数据
    weather.value = {
      city: props.defaultCity,
      temp: '22',
      text: '晴',
      icon: 'wi:day-sunny',
      iconCode: '01',
      humidity: '65%',
      windDir: '东南风',
    };
  } finally {
    weatherLoading.value = false;
  }
}

// 组件挂载时获取天气
onMounted(() => {
  if (props.showWeather) {
    fetchWeather();
  }
});
</script>

<template>
  <div class="workbench-welcome relative overflow-hidden rounded-lg">
    <!-- 内容区 -->
    <div class="welcome-content relative z-10 px-6 py-4">
      <div class="flex items-center justify-between">
        <!-- 左侧：用户问候 -->
        <div class="flex-1">
          <div class="mb-4 flex items-end gap-4">
            <h2 class="text-2xl font-bold text-white">
              {{ timeGreeting }}，{{
                userStore.userInfo?.realName || userStore.userInfo?.username
              }}
            </h2>
            <p class="text-base text-white/90">
              {{ greeting }}
            </p>
          </div>
          <div class="flex items-center gap-4 text-sm text-white/80">
            <span class="flex items-center gap-1">
              <iconify-icon icon="carbon:calendar" class="text-lg" />
              {{ currentDate }}
            </span>
            <span class="flex items-center gap-1">
              <iconify-icon icon="carbon:events" class="text-lg" />
              {{ lunarDate }}
            </span>
          </div>
        </div>

        <!-- 右侧：天气信息 -->
        <div
          v-if="showWeather && weather"
          class="weather-info relative flex items-center gap-6 overflow-hidden"
        >
          <!-- 左侧：天气示意图 -->
          <div class="weather-illustration relative flex-shrink-0">
            <!-- 晴天：太阳和云朵 -->
            <template v-if="weatherType === 'clear'">
              <div class="weather-icon-clear">
                <div class="sun-large"></div>
                <div class="cloud-decorative cloud-left"></div>
              </div>
            </template>

            <!-- 多云：云朵 -->
            <template v-else-if="weatherType === 'cloudy'">
              <div class="weather-icon-cloudy">
                <div class="cloud-large cloud-main"></div>
                <div class="cloud-decorative cloud-right"></div>
              </div>
            </template>

            <!-- 雨天：云朵和雨滴 -->
            <template v-else-if="weatherType === 'rainy'">
              <div class="weather-icon-rainy">
                <div class="cloud-large cloud-rainy"></div>
                <div class="rain-drops">
                  <div class="rain-drop"></div>
                  <div class="rain-drop"></div>
                  <div class="rain-drop"></div>
                  <div class="rain-drop"></div>
                </div>
              </div>
            </template>

            <!-- 雪天：云朵和雪花 -->
            <template v-else-if="weatherType === 'snowy'">
              <div class="weather-icon-snowy">
                <div class="cloud-large cloud-snowy"></div>
                <div class="snow-flakes">
                  <div class="snow-flake">❄</div>
                  <div class="snow-flake">❄</div>
                  <div class="snow-flake">❄</div>
                </div>
              </div>
            </template>

            <!-- 雾霾：朦胧云朵 -->
            <template v-else-if="weatherType === 'foggy'">
              <div class="weather-icon-foggy">
                <div class="cloud-large cloud-foggy"></div>
                <div class="fog-layer"></div>
              </div>
            </template>

            <!-- 默认：晴天 -->
            <template v-else>
              <div class="weather-icon-clear">
                <div class="sun-large"></div>
                <div class="cloud-decorative cloud-left"></div>
              </div>
            </template>
          </div>

          <!-- 右侧：天气数据 -->
          <div class="weather-data flex flex-1 items-start gap-6">
            <div class="relative z-10 flex flex-col">
              <div class="mb-1 flex items-center gap-2">
                <iconify-icon
                  :icon="weather.icon"
                  class="text-5xl text-white"
                />
                <div class="text-4xl font-bold text-white">
                  {{ weather.temp }}°
                </div>
              </div>
              <div class="text-sm font-medium text-white/95">
                {{ weather.text }}
              </div>
            </div>
            <div class="weather-details relative z-10 text-sm text-white/85">
              <div class="mb-1 text-sm font-medium">{{ weather.city }}</div>
              <div v-if="weather.humidity" class="mb-0.5 text-xs">
                湿度 {{ weather.humidity }}
              </div>
              <div v-if="weather.windDir" class="text-xs">
                {{ weather.windDir }}
              </div>
            </div>
          </div>
        </div>

        <!-- 天气加载中 -->
        <div
          v-else-if="showWeather && weatherLoading"
          class="flex items-center gap-2 text-white/80"
        >
          <iconify-icon icon="line-md:loading-twotone-loop" class="text-2xl" />
          <span class="text-sm">加载天气中...</span>
        </div>

        <!-- 装饰图标 -->
        <div v-else class="flex items-center">
          <iconify-icon
            icon="carbon:sun"
            class="animate-spin-slow text-6xl text-white/20"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes spin-slow {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

@keyframes sun-pulse {
  0%,
  100% {
    opacity: 0.8;
    transform: scale(1);
  }

  50% {
    opacity: 1;
    transform: scale(1.1);
  }
}

@keyframes sun-rotate {
  from {
    transform: translate(-50%, -50%) rotate(0deg);
  }

  to {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

@keyframes float-small {
  0%,
  100% {
    transform: translateY(0) translateX(0);
  }

  50% {
    transform: translateY(-10px) translateX(5px);
  }
}

@keyframes rain-fall-small {
  0% {
    opacity: 1;
    transform: translateY(0);
  }

  100% {
    opacity: 0;
    transform: translateY(100px);
  }
}

@keyframes snow-fall-small {
  0% {
    opacity: 1;
    transform: translateY(0) rotate(0deg);
  }

  100% {
    opacity: 0.3;
    transform: translateY(100px) rotate(180deg);
  }
}

@keyframes fog-move-small {
  0%,
  100% {
    opacity: 0.4;
    transform: translateX(-5%);
  }

  50% {
    opacity: 0.7;
    transform: translateX(5%);
  }
}

/* 动画 */
@keyframes sun-glow {
  0%,
  100% {
    box-shadow:
      0 0 30px rgb(255 215 0 / 60%),
      0 0 60px rgb(255 215 0 / 40%),
      inset -10px -10px 20px rgb(255 200 0 / 50%);
  }

  50% {
    box-shadow:
      0 0 40px rgb(255 215 0 / 80%),
      0 0 80px rgb(255 215 0 / 60%),
      inset -10px -10px 20px rgb(255 200 0 / 70%);
  }
}

@keyframes cloud-float {
  0%,
  100% {
    transform: translateY(0) translateX(0);
  }

  50% {
    transform: translateY(-8px) translateX(5px);
  }
}

@keyframes rain-fall {
  0% {
    opacity: 1;
    transform: translateY(0);
  }

  100% {
    opacity: 0.3;
    transform: translateY(40px);
  }
}

@keyframes snow-fall {
  0% {
    opacity: 1;
    transform: translateY(0) rotate(0deg);
  }

  100% {
    opacity: 0.2;
    transform: translateY(50px) rotate(360deg);
  }
}

@keyframes fog-move {
  0%,
  100% {
    opacity: 0.3;
    transform: translateX(-5%);
  }

  50% {
    opacity: 0.6;
    transform: translateX(5%);
  }
}

/* 响应式 */
@media (width <= 768px) {
  .welcome-content {
    padding: 16px;
  }

  .workbench-welcome h2 {
    font-size: 18px;
  }

  .workbench-welcome p {
    font-size: 14px;
  }

  .weather-illustration {
    width: 70px;
    height: 70px;
  }

  .weather-data {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
    min-width: auto;
  }

  .sun-large {
    width: 50px;
    height: 50px;
  }

  .sun-large::before {
    width: 38px;
    height: 38px;
  }

  .cloud-main,
  .cloud-rainy,
  .cloud-snowy,
  .cloud-foggy {
    width: 50px;
    height: 25px;
  }
}

/* 固定背景渐变 - 蓝色系（与原型一致，不随天气变化） */
.workbench-welcome {
  /* 固定蓝色渐变背景，参考原型颜色 */
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 50%, #1d4ed8 100%);
  box-shadow:
    0 4px 6px -1px rgb(0 0 0 / 10%),
    0 2px 4px -1px rgb(0 0 0 / 6%);
}

/* 天气框装饰 */
.weather-decoration {
  z-index: 0;
  pointer-events: none;
}

/* 太阳效果 */
.sun {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 60px;
  height: 60px;
  background: radial-gradient(
    circle,
    rgb(255 255 255 / 40%) 0%,
    transparent 70%
  );
  border-radius: 50%;
  animation: sun-pulse 3s ease-in-out infinite;
}

.sun::before {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 40px;
  height: 40px;
  content: '';
  background: rgb(255 255 255 / 30%);
  border-radius: 50%;
  box-shadow: 0 0 20px rgb(255 255 255 / 50%);
  transform: translate(-50%, -50%);
  animation: sun-rotate 8s linear infinite;
}

/* 小云朵 */
.small-cloud {
  position: absolute;
  background: rgb(255 255 255 / 15%);
  border-radius: 50px;
  animation: float-small 10s ease-in-out infinite;
}

.cloud-s1 {
  top: 15px;
  right: 20px;
  width: 50px;
  height: 20px;
  animation-delay: 0s;
}

.cloud-s1::before,
.cloud-s1::after {
  position: absolute;
  content: '';
  background: rgb(255 255 255 / 15%);
  border-radius: 50%;
}

.cloud-s1::before {
  top: -12px;
  left: 10px;
  width: 25px;
  height: 25px;
}

.cloud-s1::after {
  top: -8px;
  right: 8px;
  width: 20px;
  height: 20px;
}

.cloud-s2 {
  right: 40px;
  bottom: 20px;
  width: 40px;
  height: 16px;
  animation-delay: 5s;
}

.cloud-s2::before,
.cloud-s2::after {
  position: absolute;
  content: '';
  background: rgb(255 255 255 / 15%);
  border-radius: 50%;
}

.cloud-s2::before {
  top: -10px;
  left: 8px;
  width: 20px;
  height: 20px;
}

.cloud-s2::after {
  top: -6px;
  right: 6px;
  width: 16px;
  height: 16px;
}

/* 小雨滴 */
.small-rain {
  position: absolute;
  width: 1.5px;
  height: 15px;
  background: linear-gradient(to bottom, transparent, rgb(255 255 255 / 40%));
  animation: rain-fall-small 1.5s linear infinite;
}

.rain-s1 {
  top: 0;
  right: 30%;
  animation-delay: 0s;
}

.rain-s2 {
  top: 0;
  right: 50%;
  animation-delay: 0.5s;
}

.rain-s3 {
  top: 0;
  right: 70%;
  animation-delay: 1s;
}

/* 小雪花 */
.small-snow {
  position: absolute;
  font-size: 16px;
  color: rgb(255 255 255 / 60%);
  animation: snow-fall-small 5s linear infinite;
}

.snow-s1 {
  top: 0;
  right: 25%;
  animation-delay: 0s;
}

.snow-s2 {
  top: 0;
  right: 50%;
  font-size: 14px;
  animation-delay: 1.5s;
}

.snow-s3 {
  top: 0;
  right: 75%;
  font-size: 18px;
  animation-delay: 3s;
}

/* 小雾气 */
.small-fog {
  position: absolute;
  width: 100%;
  height: 50%;
  background: linear-gradient(
    to bottom,
    transparent,
    rgb(255 255 255 / 10%),
    transparent
  );
  animation: fog-move-small 8s ease-in-out infinite;
}

.fog-s1 {
  top: 25%;
}

.animate-spin-slow {
  animation: spin-slow 20s linear infinite;
}

/* 天气信息样式 */
.weather-info {
  min-width: 360px;
  padding: 4px 8px;
  background: rgb(255 255 255 / 12%);
  border: 1px solid rgb(255 255 255 / 25%);
  border-radius: 16px;
  box-shadow: 0 8px 16px rgb(0 0 0 / 10%);
  backdrop-filter: blur(12px);
}

.weather-illustration {
  width: 90px;
  height: 90px;
}

.weather-data {
  min-width: 220px;
}

.weather-details > div {
  line-height: 1.8;
}

/* 天气示意图样式 - 晴天 */
.weather-icon-clear {
  position: relative;
  width: 100%;
  height: 100%;
}

.sun-large {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 60px;
  height: 60px;
  background: radial-gradient(
    circle,
    #ffd700 0%,
    #ffed4e 30%,
    #ffa500 60%,
    transparent 100%
  );
  border-radius: 50%;
  box-shadow:
    0 0 20px rgb(255 215 0 / 60%),
    0 0 40px rgb(255 215 0 / 40%),
    inset -8px -8px 15px rgb(255 200 0 / 50%);
  transform: translate(-50%, -50%);
  animation: sun-glow 3s ease-in-out infinite;
}

.sun-large::before {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 45px;
  height: 45px;
  content: '';
  background: radial-gradient(circle, #fff 0%, #ffd700 50%, transparent 100%);
  border-radius: 50%;
  box-shadow: 0 0 12px rgb(255 255 255 / 80%);
  transform: translate(-50%, -50%);
}

.cloud-decorative {
  position: absolute;
  background: rgb(255 255 255 / 40%);
  border-radius: 50px;
  filter: blur(2px);
}

.cloud-left {
  top: 15px;
  left: 8px;
  width: 40px;
  height: 16px;
  animation: cloud-float 8s ease-in-out infinite;
}

.cloud-left::before,
.cloud-left::after {
  position: absolute;
  content: '';
  background: rgb(255 255 255 / 40%);
  border-radius: 50%;
  filter: blur(1px);
}

.cloud-left::before {
  top: -12px;
  left: 6px;
  width: 24px;
  height: 24px;
}

.cloud-left::after {
  top: -8px;
  right: 4px;
  width: 20px;
  height: 20px;
}

/* 天气示意图样式 - 多云 */
.weather-icon-cloudy {
  position: relative;
  width: 100%;
  height: 100%;
}

.cloud-large {
  position: absolute;
  background: linear-gradient(
    135deg,
    rgb(255 255 255 / 50%) 0%,
    rgb(255 255 255 / 30%) 100%
  );
  border-radius: 50px;
  box-shadow: 0 4px 8px rgb(0 0 0 / 10%);
}

.cloud-main {
  top: 30%;
  left: 20%;
  width: 55px;
  height: 28px;
  animation: cloud-float 10s ease-in-out infinite;
}

.cloud-main::before,
.cloud-main::after {
  position: absolute;
  content: '';
  background: linear-gradient(
    135deg,
    rgb(255 255 255 / 50%) 0%,
    rgb(255 255 255 / 30%) 100%
  );
  border-radius: 50%;
  box-shadow: 0 2px 4px rgb(0 0 0 / 10%);
}

.cloud-main::before {
  top: -16px;
  left: 12px;
  width: 32px;
  height: 32px;
}

.cloud-main::after {
  top: -12px;
  right: 8px;
  width: 28px;
  height: 28px;
}

.cloud-right {
  top: 40px;
  right: 4px;
  width: 36px;
  height: 14px;
  animation: cloud-float 12s ease-in-out infinite 2s;
}

.cloud-right::before {
  top: -12px;
  left: 8px;
  width: 25px;
  height: 25px;
  content: '';
  background: rgb(255 255 255 / 40%);
  border-radius: 50%;
}

/* 天气示意图样式 - 雨天 */
.weather-icon-rainy {
  position: relative;
  width: 100%;
  height: 100%;
}

.cloud-rainy {
  top: 25%;
  left: 15%;
  width: 60px;
  height: 32px;
  background: linear-gradient(
    135deg,
    rgb(150 150 150 / 60%) 0%,
    rgb(100 100 100 / 40%) 100%
  );
  animation: cloud-float 8s ease-in-out infinite;
}

.cloud-rainy::before,
.cloud-rainy::after {
  background: linear-gradient(
    135deg,
    rgb(150 150 150 / 60%) 0%,
    rgb(100 100 100 / 40%) 100%
  );
}

.rain-drops {
  position: absolute;
  top: 65%;
  left: 50%;
  width: 50px;
  height: 35px;
  transform: translateX(-50%);
}

.rain-drop {
  position: absolute;
  width: 1.5px;
  height: 16px;
  background: linear-gradient(
    to bottom,
    rgb(173 216 230 / 80%) 0%,
    rgb(135 206 250 / 60%) 100%
  );
  border-radius: 2px;
  animation: rain-fall 1s linear infinite;
}

.rain-drop:nth-child(1) {
  left: 8px;
  animation-delay: 0s;
}

.rain-drop:nth-child(2) {
  left: 20px;
  animation-delay: 0.2s;
}

.rain-drop:nth-child(3) {
  left: 32px;
  animation-delay: 0.4s;
}

.rain-drop:nth-child(4) {
  left: 44px;
  animation-delay: 0.6s;
}

/* 天气示意图样式 - 雪天 */
.weather-icon-snowy {
  position: relative;
  width: 100%;
  height: 100%;
}

.cloud-snowy {
  top: 25%;
  left: 15%;
  width: 60px;
  height: 32px;
  background: linear-gradient(
    135deg,
    rgb(200 200 220 / 70%) 0%,
    rgb(180 180 200 / 50%) 100%
  );
  animation: cloud-float 10s ease-in-out infinite;
}

.cloud-snowy::before,
.cloud-snowy::after {
  background: linear-gradient(
    135deg,
    rgb(200 200 220 / 70%) 0%,
    rgb(180 180 200 / 50%) 100%
  );
}

.snow-flakes {
  position: absolute;
  top: 60%;
  left: 50%;
  width: 65px;
  height: 40px;
  transform: translateX(-50%);
}

.snow-flake {
  position: absolute;
  font-size: 16px;
  color: rgb(255 255 255 / 90%);
  text-shadow: 0 0 4px rgb(255 255 255 / 80%);
  animation: snow-fall 3s linear infinite;
}

.snow-flake:nth-child(1) {
  left: 12px;
  font-size: 14px;
  animation-delay: 0s;
}

.snow-flake:nth-child(2) {
  left: 32px;
  font-size: 18px;
  animation-delay: 1s;
}

.snow-flake:nth-child(3) {
  left: 52px;
  font-size: 13px;
  animation-delay: 2s;
}

/* 天气示意图样式 - 雾霾 */
.weather-icon-foggy {
  position: relative;
  width: 100%;
  height: 100%;
}

.cloud-foggy {
  top: 30%;
  left: 20%;
  width: 55px;
  height: 28px;
  background: linear-gradient(
    135deg,
    rgb(180 180 180 / 50%) 0%,
    rgb(150 150 150 / 30%) 100%
  );
  animation: cloud-float 12s ease-in-out infinite;
}

.fog-layer {
  position: absolute;
  top: 50%;
  left: 0;
  width: 100%;
  height: 40%;
  background: linear-gradient(
    to bottom,
    transparent 0%,
    rgb(200 200 200 / 20%) 50%,
    transparent 100%
  );
  border-radius: 50%;
  filter: blur(8px);
  animation: fog-move 6s ease-in-out infinite;
}

/* 慢速旋转动画 */
</style>
