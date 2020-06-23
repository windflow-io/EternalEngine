import Vue from '/vendor/vue/vue.esm.browser.js';

export function loadComponent(name, componentUrl, templateUrl, callback) {
    console.log(name, componentUrl, templateUrl, callback)
    Vue.component(name, function (resolve, reject) {
        console.log("Needs to load component")
        import (componentUrl).then((module) => {
            if (templateUrl !== null && templateUrl !== undefined) {
                fetch(templateUrl).then(response => {
                    if (response.ok)
                        return response.text();
                    else {
                        throw new Error("Could not load template " + templateUrl)
                    }
                }).then(templateData => {

                    module.default.template = templateData;
                    if (callback) {
                        callback(module, resolve, reject)
                    } else {
                        resolve(module);
                    }
                }).catch(error => {
                    console.error(error);
                    reject(error);
                });
            } else {

                if (callback) {

                    callback(module, resolve, reject)
                } else {
                    resolve(module);
                }
            }
        })
    });
}
