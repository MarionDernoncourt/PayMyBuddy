import Vue from "vue";
import Router from "vue-router";

import Login from "../views/Auth/Login.vue";
import Register from "../views/Auth/Register.vue";

import Transfer from "../views/Account/Transfer.vue";
import AddFriend from "../views/Account/AddFriend.vue";
import EditProfile from "../views/Account/EditProfile.vue";

Vue.use(Router);

const router = new Router({
  mode: "history",
  routes: [
    {
      path: "/",
      redirect: "/login",
    },
    {
      path: "/login",
      name: "loginPage",
      component: Login,
    },
    {
      path: "/register",
      name: "registerPage",
      component: Register,
    },
    {
      path: "/transfer",
      name: "transferPage",
      component: Transfer,
      meta: { requiresAuth: true },
    },
    {
      path: "/addFriend",
      name: "addFriend",
      component: AddFriend,
      meta: { requiresAuth: true },
    },
    {
      path: "/edit-profile",
      name: "edit-profile",
      component: EditProfile,
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach((to, from, next) => {
 const token = localStorage.getItem("token");
const expiresAt = localStorage.getItem("tokenExpiresAt");

// Supprimer token expiré
if (token && expiresAt && Date.now() > parseInt(expiresAt)) {
  console.warn("Token expiré, suppression du stockage");
  localStorage.removeItem("token");
  localStorage.removeItem("tokenExpiresAt");
}

// Redéterminer isAuthenticated après possible suppression
const isAuthenticated = !!localStorage.getItem("token");


  if (
    to.matched.some((record) => record.meta.requiresAuth) &&
    !isAuthenticated
  ) {
    // Si la page requiert auth mais user non connecté => vers login
    next("/login");
  } else if (
    (to.path === "/login" || to.path === "/register") &&
    isAuthenticated
  ) {
    // Si user connecté essaie d'aller sur login ou register => redirige vers transfer
    next("/transfer");
  } else {
    next();
  }
});

export default router;
