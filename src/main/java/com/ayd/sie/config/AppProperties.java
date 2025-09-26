package com.ayd.sie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final Security security = new Security();
    private final Business business = new Business();

    public static class Jwt {
        private String secret;
        private long expiration;
        private long refreshExpiration;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }

        public long getRefreshExpiration() {
            return refreshExpiration;
        }

        public void setRefreshExpiration(long refreshExpiration) {
            this.refreshExpiration = refreshExpiration;
        }
    }

    public static class Security {
        private int maxLoginAttempts;
        private int lockoutDuration;
        private long passwordResetExpiration;

        public int getMaxLoginAttempts() {
            return maxLoginAttempts;
        }

        public void setMaxLoginAttempts(int maxLoginAttempts) {
            this.maxLoginAttempts = maxLoginAttempts;
        }

        public int getLockoutDuration() {
            return lockoutDuration;
        }

        public void setLockoutDuration(int lockoutDuration) {
            this.lockoutDuration = lockoutDuration;
        }

        public long getPasswordResetExpiration() {
            return passwordResetExpiration;
        }

        public void setPasswordResetExpiration(long passwordResetExpiration) {
            this.passwordResetExpiration = passwordResetExpiration;
        }
    }

    public static class Business {
        private final Loyalty loyalty = new Loyalty();

        public static class Loyalty {
            private final Silver silver = new Silver();
            private final Gold gold = new Gold();
            private final Diamond diamond = new Diamond();

            public static class Silver {
                private int minDeliveries;
                private int maxDeliveries;
                private double discount;

                public int getMinDeliveries() {
                    return minDeliveries;
                }

                public void setMinDeliveries(int minDeliveries) {
                    this.minDeliveries = minDeliveries;
                }

                public int getMaxDeliveries() {
                    return maxDeliveries;
                }

                public void setMaxDeliveries(int maxDeliveries) {
                    this.maxDeliveries = maxDeliveries;
                }

                public double getDiscount() {
                    return discount;
                }

                public void setDiscount(double discount) {
                    this.discount = discount;
                }
            }

            public static class Gold {
                private int minDeliveries;
                private int maxDeliveries;
                private double discount;

                public int getMinDeliveries() {
                    return minDeliveries;
                }

                public void setMinDeliveries(int minDeliveries) {
                    this.minDeliveries = minDeliveries;
                }

                public int getMaxDeliveries() {
                    return maxDeliveries;
                }

                public void setMaxDeliveries(int maxDeliveries) {
                    this.maxDeliveries = maxDeliveries;
                }

                public double getDiscount() {
                    return discount;
                }

                public void setDiscount(double discount) {
                    this.discount = discount;
                }
            }

            public static class Diamond {
                private int minDeliveries;
                private double discount;
                private int freeCancellations;

                public int getMinDeliveries() {
                    return minDeliveries;
                }

                public void setMinDeliveries(int minDeliveries) {
                    this.minDeliveries = minDeliveries;
                }

                public double getDiscount() {
                    return discount;
                }

                public void setDiscount(double discount) {
                    this.discount = discount;
                }

                public int getFreeCancellations() {
                    return freeCancellations;
                }

                public void setFreeCancellations(int freeCancellations) {
                    this.freeCancellations = freeCancellations;
                }
            }

            public Silver getSilver() {
                return silver;
            }

            public Gold getGold() {
                return gold;
            }

            public Diamond getDiamond() {
                return diamond;
            }
        }

        public Loyalty getLoyalty() {
            return loyalty;
        }
    }

    public Jwt getJwt() {
        return jwt;
    }

    public Security getSecurity() {
        return security;
    }

    public Business getBusiness() {
        return business;
    }
}