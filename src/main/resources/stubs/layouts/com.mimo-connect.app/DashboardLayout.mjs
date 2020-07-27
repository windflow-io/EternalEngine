import {FlowArea, FlowIcon} from '/modules/coreComponents.mjs'
export default {
    name: 'DashboardLayout',
    components: {FlowArea, FlowIcon},
    /**@TODO: Pull the themes from a Mixin from the Vue store **/
    template:
        `<section>
            <div class="max-w-sm bg-gray-900 text-white">Menu</div>
            <div class="w-full flex flex-row text-white">               
                <div class="flex-grow bg-red-500">4G</div>
                <div class="flex-grow bg-blue-500">V-Sat</div>
                <div class="flex-grow bg-purple-500">TV</div>
            </div>
        </section>
        <flow-icon icon="bars" class="w-5 text-red-500 fill-current"></flow-icon>                 
         `
}
