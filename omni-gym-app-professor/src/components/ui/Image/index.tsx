/* eslint-disable no-restricted-imports */
import { Image as ExpoImage, ImageProps } from 'expo-image';
import { PropsWithChildren, useState } from 'react';
import { View } from 'react-native';

import Shimmer from '../Shimmer';

type Props = {
  showShimmer?: boolean;
} & ImageProps;

const Image = ({
  children,
  style,
  showShimmer = false,
  ...props
}: PropsWithChildren<Props>) => {
  const [isLoading, setIsLoading] = useState(false);

  const onLoad = () => {
    setIsLoading(false);
  };

  return (
    <View style={[{ overflow: 'hidden' }, style]}>
      <ExpoImage style={{ flex: 1 }} onLoad={onLoad} {...props} />

      {isLoading && showShimmer && (
        <View
          style={{
            bottom: 0,
            left: 0,
            position: 'absolute',
            right: 0,
            top: 0,
          }}
        >
          <Shimmer />
        </View>
      )}

      {children}
    </View>
  );
};

export default Image;
