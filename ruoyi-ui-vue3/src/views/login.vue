<template>
  <div class="login-main">
    <div class="login-left">
      <div class="left-content">
        <h1 class="ls3">欢迎回来</h1>
        <p class="welcome-subtitle">登录以继续访问 {{ title }} </p>
        <el-divider/>
        <div class="feature-list">
          <div class="feature-item">
            <span class="feature-icon"><el-icon><OfficeBuilding/></el-icon></span>
            <span class="ls2">智能资产管理</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon"><el-icon><DataLine/></el-icon></span>
            <span class="ls2">数据可视化分析</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon"><el-icon><CircleCheck/></el-icon></span>
            <span class="ls2">安全权限管控</span>
          </div>
        </div>
      </div>
    </div>
    <div class="login-right">
      <div class="login-card">
        <h3 class="login-title ls2">{{ title }}</h3>
        <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input
                v-model="loginForm.username"
                type="text"
                size="large"
                auto-complete="off"
                placeholder="请输入账号"
                class="login-input"
            >
              <template #prefix>
                <el-icon class="input-icon">
                  <User/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
                v-model="loginForm.password"
                type="password"
                size="large"
                auto-complete="off"
                placeholder="请输入密码"
                class="login-input"
                @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon class="input-icon">
                  <Lock/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="captcha-wrapper">
              <el-input
                  v-model="loginForm.code"
                  size="large"
                  auto-complete="off"
                  placeholder="请输入验证码"
                  class="captcha-input"
                  @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <svg-icon icon-class="validCode" class="input-icon"/>
                </template>
              </el-input>
              <div class="captcha-image">
                <img :src="codeUrl" @click="getCode" class="captcha-img" title="点击更换验证码"/>
              </div>
            </div>
          </el-form-item>
          <div class="form-options">
            <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住密码</el-checkbox>
            <span class="forgot-password" v-if="false">忘记密码？</span>
          </div>
          <el-form-item>
            <el-button
                :loading="loading"
                size="large"
                type="primary"
                class="login-btn ls2"
                @click.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>
          <div class="register-link" v-if="register">
            <span>还没有账号？</span>
            <router-link class="link-type" :to="'/register'">立即注册</router-link>
          </div>
        </el-form>
      </div>
      <div class="el-login-footer">
        <span>{{ footerContent }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {getCodeImg} from "@/api/login"
import Cookies from "js-cookie"
import {encrypt, decrypt} from "@/utils/jsencrypt"
import useUserStore from '@/store/modules/user'
import defaultSettings from '@/settings'
import type {CaptchaInfoResult} from '@/types/api/login'
import type {LoginForm} from '@/types/api/login'

const title = import.meta.env.VITE_APP_TITLE
const footerContent = defaultSettings.footerContent
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const {proxy} = getCurrentInstance()

const loginForm = ref<LoginForm>({
  username: "admin",
  password: "admin123",
  rememberMe: false,
  code: "",
  uuid: ""
})

const loginRules = {
  username: [{required: true, trigger: "blur", message: "请输入您的账号"}],
  password: [{required: true, trigger: "blur", message: "请输入您的密码"}],
  code: [{required: true, trigger: "change", message: "请输入验证码"}]
}

const codeUrl = ref("")
const loading = ref(false)
const captchaEnabled = ref(true)
const register = ref(false)
const redirect = ref<string | undefined>(undefined)

watch(route, (newRoute: any) => {
  redirect.value = (newRoute.query && newRoute.query.redirect) as string | undefined
}, {immediate: true})

function handleLogin(): void {
  proxy.$refs.loginRef.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, {expires: 30})
        Cookies.set("password", encrypt(loginForm.value.password), {expires: 30})
        Cookies.set("rememberMe", loginForm.value.rememberMe, {expires: 30})
      } else {
        Cookies.remove("username")
        Cookies.remove("password")
        Cookies.remove("rememberMe")
      }
      userStore.login(loginForm.value).then(() => {
        const query = route.query
        const otherQueryParams = Object.keys(query).reduce((acc: Record<string, any>, cur) => {
          if (cur !== "redirect") {
            acc[cur] = query[cur]
          }
          return acc
        }, {})
        router.push({path: redirect.value || "/", query: otherQueryParams})
      }).catch(() => {
        loading.value = false
        if (captchaEnabled.value) {
          getCode()
        }
      })
    }
  })
}

function getCode(): void {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img
      loginForm.value.uuid = res.uuid
    }
  })
}

function getCookie(): void {
  const username = Cookies.get("username")
  const password = Cookies.get("password")
  const rememberMe = Cookies.get("rememberMe")
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password),
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
  }
}

getCode()
getCookie()
</script>

<style lang='scss' scoped>
.login-main {
  display: flex;
  overflow: hidden;
  height: 100%;

  // ========== 左侧样式 ==========
  .login-left {
    width: 55%;
    background: linear-gradient(135deg, #042654 0%, #0e498f 50%, #3778c8 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -30%;
      width: 80%;
      height: 200%;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 50%;
      transform: rotate(15deg);
    }

    &::after {
      content: '';
      position: absolute;
      bottom: -40%;
      left: -20%;
      width: 60%;
      height: 150%;
      background: rgba(255, 255, 255, 0.03);
      border-radius: 50%;
    }

    .left-content {
      position: relative;
      color: var(--el-bg-color);
      max-width: 480px;
      width: 100%;
    }

    .welcome-subtitle {
      opacity: 0.7;
    }

    .decoration-line {
      width: 60px;
      height: 3px;
      background: rgba(255, 255, 255, 0.6);
      border-radius: 2px;
      margin-bottom: 40px;
    }

    .feature-list {
      display: flex;
      flex-direction: column;
      gap: 15px;

      .feature-item {
        display: flex;
        align-items: center;
        gap: 14px;
        font-size: 15px;
        opacity: 0.9;
        transition: opacity 0.3s;

        &:hover {
          opacity: 1;
        }

        .feature-icon {
          font-size: 20px;
          width: 36px;
          height: 36px;
          background: rgba(255, 255, 255, 0.15);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }
    }
  }

  // ========== 右侧样式 ==========
  .login-right {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    padding: 40px;
    position: relative;

    .login-card {
      background: #ffffff;
      border-radius: 8px;
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
      padding: 30px 20px 10px;
      width: 100%;
      max-width: 420px;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 24px 72px rgba(0, 0, 0, 0.12);
      }
    }


    .login-title {
      text-align: center;
      color: var(--el-text-color-primary);
      margin: 15px 0px;
    }
  }

  // ========== 表单样式 ==========
  .login-form {
    width: 100%;

    .login-input {
      :deep(.el-input__wrapper) {
        height: 46px;
        border-radius: 10px;
        border: 1.5px solid #e8ecf1;
        transition: all 0.3s ease;
        box-shadow: none;
        padding: 0 16px;

        &:hover {
          border-color: #b3c9e0;
        }

        &.is-focus {
          border-color: var(--el-color-primary);
          box-shadow: 0 0 0 4px rgba(74, 144, 217, 0.1);
        }

        .el-input__prefix {
          margin-right: 10px;

          .input-icon {
            font-size: 18px;
            color: #9aa3b5;
          }
        }

        .el-input__inner {
          font-size: 14px;
          height: 46px;
          line-height: 46px;
        }
      }
    }

    .captcha-wrapper {
      display: flex;
      gap: 10px;
      align-items: center;

      .captcha-input {
        flex: 1;

        :deep(.el-input__wrapper) {
          height: 46px;
          border-radius: 10px;
          border: 1.5px solid #e8ecf1;
          transition: all 0.3s ease;
          box-shadow: none;
          padding: 0 15px;

          &:hover {
            border-color: #b3c9e0;
          }

          &.is-focus {
            border-color: var(--el-color-primary);
            box-shadow: 0 0 0 4px rgba(74, 144, 217, 0.1);
          }

          .el-input__prefix {
            margin-right: 10px;

            .input-icon {
              font-size: 18px;
              color: #9aa3b5;
            }
          }

          .el-input__inner {
            font-size: 14px;
            height: 46px;
            line-height: 46px;
          }
        }
      }

      .captcha-image {
        flex-shrink: 0;

        .captcha-img {
          margin-top: 6px;
          height: 40px;
          border-radius: 5px;
          cursor: pointer;
          border: 1px solid #e8ecf1;
          transition: border-color 0.3s;
          background: #f5f7fa;

          &:hover {
            border-color: #b3c9e0;
          }
        }
      }
    }

    .form-options {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 16px 0 24px 0;

      .remember-me {
        :deep(.el-checkbox__label) {
          font-size: 14px;
          color: #606266;
        }

        :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
          background-color: var(--el-color-primary);
          border-color: var(--el-color-primary);
        }
      }

      .forgot-password {
        font-size: 13px;
        color: var(--el-color-primary);
        cursor: pointer;
        transition: color 0.3s;

        &:hover {
          color: #1a3a5c;
        }
      }
    }

    .login-btn {
      width: 100%;
      height: 45px;
      border-radius: 10px;
      font-size: 16px;
      background: linear-gradient(135deg, var(--el-color-primary) 0%, #2d6a9f 100%);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 8px 24px rgba(74, 144, 217, 0.35);
      }

      &:active {
        transform: translateY(0);
      }

      &.is-loading {
        opacity: 0.8;
      }
    }

    .register-link {
      text-align: center;
      margin-top: 20px;
      font-size: 14px;
      color: #8c8f9c;

    }
  }

  // ========== 底部样式 ==========
  .el-login-footer {
    position: absolute;
    bottom: 24px;
    left: 0;
    width: 100%;
    text-align: center;
    color: #aab0bc;
    font-size: 12px;
    letter-spacing: 1px;
    font-family: Arial, sans-serif;
    padding: 0 20px;
  }

  // ========== 表单项间距 ==========
  .el-form-item {
    margin-bottom: 20px;
  }
}

// ========== 暗色主题 ==========
html.dark {
  .login-main {
    .login-left {
      background: linear-gradient(135deg, #0d1b2a 0%, #1b3a5c 50%, #2d6a9f 100%);
    }

    .login-right {
      background: #1a1a2e;

      .login-card {
        background: #1e1e3a;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);

        &:hover {
          box-shadow: 0 24px 72px rgba(0, 0, 0, 0.5);
        }
      }

      .login-header {
        .login-title {
          color: #e8ecf1;
        }

        .login-subtitle {
          color: #8c8f9c;
        }
      }
    }

    .login-form {
      .login-input :deep(.el-input__wrapper) {
        background: rgba(255, 255, 255, 0.06);
        border-color: rgba(255, 255, 255, 0.1);

        &:hover {
          border-color: rgba(255, 255, 255, 0.2);
        }

        .el-input__inner {
          color: #e8ecf1;
        }

        .el-input__prefix .input-icon {
          color: #8c8f9c;
        }
      }

      .captcha-wrapper {
        .captcha-input :deep(.el-input__wrapper) {
          background: rgba(255, 255, 255, 0.06);
          border-color: rgba(255, 255, 255, 0.1);

          &:hover {
            border-color: rgba(255, 255, 255, 0.2);
          }

          .el-input__inner {
            color: #e8ecf1;
          }

          .el-input__prefix .input-icon {
            color: #8c8f9c;
          }
        }

        .captcha-image .captcha-img {
          background: rgba(255, 255, 255, 0.06);
          border-color: rgba(255, 255, 255, 0.1);
        }
      }

      .form-options {
        .remember-me :deep(.el-checkbox__label) {
          color: #aab0bc;
        }
      }

      .register-link {
        color: #8c8f9c;

        .link-type {
          color: var(--el-color-primary);

          &:hover {
            color: #6aafff;
          }
        }
      }
    }

    .el-login-footer {
      color: #5a5f6c;
    }
  }
}

// ========== 响应式 ==========
@media screen and (max-width: 992px) {
  .login-main {
    .login-left {
      display: none !important;
    }

    .login-right {
      width: 100%;
      padding: 20px;

      .login-card {
        padding: 32px 24px 24px;
        max-width: 380px;
      }
    }
  }
}

@media screen and (max-width: 480px) {
  .login-main {
    .login-right {
      padding: 16px;

      .login-card {
        padding: 24px 16px 20px;
        border-radius: 12px;
      }
    }

    .login-form {
      .login-input :deep(.el-input__wrapper) {
        height: 42px;

        .el-input__inner {
          height: 42px;
          line-height: 42px;
        }
      }

      .login-btn {
        height: 42px;
        font-size: 14px;
      }
    }
  }
}
</style>
