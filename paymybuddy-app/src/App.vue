<template>
  <div id="app">
    <headerComponent v-if="isLogged" />

    <router-view />
  </div>
</template>

<script>
import headerComponent from "@/components/Header.vue"

export default {
  components: { headerComponent },
  computed: {
    isLogged() {
      return localStorage.getItem("token")
    }
  }
}
</script>

<style>
* {
  font-family: 'Montserrat', sans-serif;
}

body,
html {
  margin: 0;
  padding: 0;
  height: 100%;
}
</style>