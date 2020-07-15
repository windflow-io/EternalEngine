/** Query String **/

import {app} from '../app/app.mjs'

export const mapQueryString = (url) => {
    let o = {}
    let kvs = url.substring(url.indexOf("?") + 1).split("&");
    for (let i in kvs) {
        let kv = kvs[i].split("=");
        o[kv[0]] = kv[1];
    }
    return o;
}

/** Component Loading **/

const registeredComponents = {};

export const loadComponent = async url => {
    const module = await import(url);

    if (!registeredComponents[module.default.name]) {
        app.component(module.default.name, module.default);
        registeredComponents[module.default.name] = module.default;
    }

    return module.default.name;
}

/** Namespacing **/

export const removeNamespace = (name) => {
    return name ? name.substring(name.lastIndexOf(".") + 1) : null;
}

export const namespaceOnly = (name) => {
    return name ? name.substring(0, name.lastIndexOf(".")) : null;
}

/** History API **/

const listeners = [];
let previousRoute = window.location.pathname;

export const pushUrl = (route) => {
    previousRoute = window.location.pathname;
    window.history.pushState(null, null, route);
    listeners.forEach(listener => listener(route, previousRoute))
}

export const addUrlListener = (callback) => {
    listeners.push(callback);
}

window.addEventListener(
    "popstate", (event) => {
        listeners.forEach(listener => listener(window.location.pathname, previousRoute))
    }
);
