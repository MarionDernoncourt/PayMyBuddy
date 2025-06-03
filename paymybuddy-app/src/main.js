import Vue from 'vue'
import App from './App.vue'
import router from './router';
import axios from 'axios'

Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')


axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});
