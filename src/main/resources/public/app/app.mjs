import Vue from '/vendor/vue/vue.esm.browser.js';

new Vue({
    el: '#app',
    beforeMount() {
        self = this;
        Vue.component("flo-layout", function (resolve, reject) {
            fetch('/api/layouts/sidebar-layout.html')
                .then((response) => response.text())
                .then((layout) => {
                    componentContent.template = layout;
                    self.doResolve(resolve);

                });
            fetch('/api/pages/' + window.location.href)
                .then((response) => response.json())
                .then((page) => {
                    componentContent.data = function() {
                        return {
                            page: page,
                            display: {}
                        }
                    }
                    self.doResolve(resolve);
                });
        });
    },
    methods: {
        doResolve(resolve) {
            if (componentContent.template && componentContent.data) {
                resolve(componentContent);
            }
        }
    }
});

let componentContent = {
    name: null,
    template: null,
    data: null,
    async beforeMount() {
        let components = this.page.components;
        for (let index = 0; index < components.length; index++) {
            let area = components[index].area;
            let component = components[index].component

            const module = await import('/api/components/' + component + '.mjs')
            const rawComponent = module.default;
            const componentName = rawComponent.name;
            Vue.component(componentName, rawComponent);
            Vue.set(this.display, area, componentName);
        }
    }
}
