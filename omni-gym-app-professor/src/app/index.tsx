import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { View } from 'react-native';
import Animated, { FadeIn } from 'react-native-reanimated';
import { scheduleOnRN } from 'react-native-worklets';

import { professorEmptyImg } from '@/assets/images';
import { Image } from '@/components/ui';
import { useAuth } from '@/contexts/useAuth';

const Intro = () => {
  const router = useRouter();
  const { loading, user } = useAuth();

  const [isFinished, setIsFinished] = useState(false);

  useEffect(() => {
    if (!isFinished || loading) {
      return;
    }

    const timeout = setTimeout(() => {
      if (user) {
        router.replace('/(main)/home');
        return;
      }

      router.replace('/(auth)/login');
    }, 500);

    return () => clearTimeout(timeout);
  }, [isFinished, loading, router, user]);

  const toggleFinished = () => {
    setIsFinished(true);
  };

  return (
    <View className="flex-1 items-center justify-center bg-primary-100 px-4">
      <Animated.View
        className="h-full w-full items-center justify-center"
        entering={FadeIn.duration(800).withCallback(finished => {
          if (finished) {
            scheduleOnRN(toggleFinished);
          }
        })}
      >
        <Image
          contentFit="contain"
          source={professorEmptyImg}
          style={{ height: '100%', width: '100%' }}
        />
      </Animated.View>
    </View>
  );
};

export default Intro;
