import { TurboModule, TurboModuleRegistry } from 'react-native';
import type { Double } from 'react-native/Libraries/Types/CodegenTypes';

interface WindowMetrics {
  insets: { top: Double; right: Double; bottom: Double; left: Double; }
  frame: { x: Double; y: Double; width: Double; height: Double; }
}

export interface Spec extends TurboModule {
  getConstants(): { initialWindowMetrics?: WindowMetrics };
  getWindowMetrics?(): Promise<null | WindowMetrics>
}

export default TurboModuleRegistry.get<Spec>('RNCSafeAreaContext');
