import {FlowLink} from '/modules/coreComponents.js'

export default {
    name: 'SideMenu',
    components: {FlowLink},
    template: `
        <nav class="flex flex-col text-white">
            <h2 class="font-bold">Menu</h2>
            <flow-link class="text-blue-500" to="/">Home Page</flow-link>
            <flow-link class="text-blue-500" to="/about">About</flow-link>
            <flow-link class="text-blue-500" to="/contact">Contact</flow-link>
            <flow-link class="text-blue-500" to="/auth">Auth</flow-link>
            <flow-link class="text-blue-500" to="/upload">Upload</flow-link>
            <flow-link class="text-blue-500" to="/gallery">Gallery</flow-link>
            <flow-link class="text-blue-500" to="/poo">404</flow-link>
        </nav>
    `
}
