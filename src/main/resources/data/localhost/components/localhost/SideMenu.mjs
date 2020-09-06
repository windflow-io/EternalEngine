import {FlowLink} from '/modules/coreComponents.mjs'

export default {
    name: 'SideMenu',
    components: {FlowLink},
    template: `
        <nav class="flex flex-col text-white">
            <h2 class="font-bold">Menu</h2>
            <flow-link class="text-blue-500" to="/">Home Page</flow-link>
            <flow-link class="text-blue-500" to="/about">About</flow-link>
            <flow-link class="text-blue-500" to="/contact">Contact</flow-link>
            <flow-link class="text-blue-500" to="/poo">404</flow-link>
        </nav>
    `
}