import {FlowIcon} from "/modules/coreComponents.mjs";

/*@TODO:Get a state (nonce) from the server and compare it later. At the moment it's a MD5 hash of 'windflow.io'*/
export default {
    name: 'GitHubAuth',
    components: {FlowIcon},
    data() {
        return {
            github: {
                base_url: 'https://github.com/login/oauth/authorize',
                client_id: '30bd3b79eb4c2e226e13',
                state: '3b696208e11d645d023ee541f3def35e',
                allow_signup: 'true',
                scope: 'read:user+user:email'
            }
        }
    },
    template: `
        <h1 class="text-4xl text-white mb-5">GitHub Authentication</h1>
        <a @click="redirect(urlBuilder())" class="cursor-pointer">
            <div class=" flex border border-white p-1 w-40 rounded-lg text-white bg-blue-700 justify-center hover:bg-blue-600 items-center text-sm">
                <flow-icon icon="brands github" class="pr-2 w-6" /> Login with Github
            </div>
        </a>
        <div class="mt-8 text-gray-500 text-sm">
            <ul>
                <li>Documentation: <a href="https://docs.github.com/en/free-pro-team@latest/developers/apps/authorizing-oauth-apps" class="text-blue-400 hover:text-blue-300">https://docs.github.com/en/free-pro-team@latest/developers/apps/authorizing-oauth-apps</a></li>
                <li>Settings: <a href="https://github.com/settings/applications/1389161" class="text-blue-400 hover:text-blue-300">https://github.com/settings/applications/1389161</a></li>
            </ul>
        </div>
    `,
    mounted() {
        let url = window.location.href;
        if (url.indexOf("code") > -1) {
            let code = this.getCodeFromUrl(url);
            let url2 = 'https://github.com/login/oauth/access_token?client_id=30bd3b79eb4c2e226e13&client_secret=43f827284f61d22e982cf0600d1bb4de30e8c580&code=' + code + '&redirect_uri=http://auth.windflow.io:8080&state=12345'
            fetch(url2).then(response => response.json()).then(data => console.log(data));
            /* @TODO: This baby needs to talk with the server */
        }
    },
    methods: {
        getCodeFromUrl(url) {
            let codePos = url.indexOf("code=");
            let codePrefix = url.substring(codePos + 5);
            let codeEndPos = codePrefix.indexOf("&");
            return codeEndPos > -1 ? codePrefix.substring(0, codeEndPos) : codePrefix;
        },
        urlBuilder: function () {
            return this.github.base_url + '?client_id=' + this.github.client_id + '&scope=' + this.github.scope + '&state=' + this.github.state + '&allow_signup=' + this.github.allow_signup
        },
        redirect(url) {
            window.location = url
        }
    }
}
