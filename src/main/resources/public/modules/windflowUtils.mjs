import Vue from '/vendor/vue/vue.esm.browser.js';

export function loadComponent(name, componentUrl, templateUrl) {
    Vue.component(name, function (resolve, reject) {
        import (componentUrl).then((module) => {
            if (templateUrl) {
                fetch(templateUrl).then(response => {
                    if (response.ok)
                        return response.text();
                    else {
                        throw new Error("Could not load template " + templateUrl)
                    }
                }).then(templateData => {
                    console.log (templateData);
                    module.default.template = templateData;
                    resolve(module);
                }).catch(error => {
                    console.error(error);
                    reject(error);
                });
            } else {
                resolve(module);
            }
        })
    });
}
