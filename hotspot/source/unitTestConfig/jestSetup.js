import '@babel/polyfill';

import { shallow, mount, render } from 'enzyme';

/**
 * enzyme settings for all tests
 */

global.shallow = shallow;
global.mount = mount;
global.render = render;
